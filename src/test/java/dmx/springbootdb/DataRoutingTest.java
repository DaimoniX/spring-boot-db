package dmx.springbootdb;

import dmx.springbootdb.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataRoutingTest {
    @Container
    public static final DockerComposeContainer<?> CONTAINER;
    private static final String JDBC_URL_TEMPLATE = "jdbc:mysql://%s:%d/tests";
    private static Connection MASTER_CONNECTION;
    private static Connection SLAVE_CONNECTION;

    static {
        CONTAINER = new DockerComposeContainer<>(new File("src/test/resources/docker-compose.yaml"))
                .withExposedService("master", 3306)
                .withExposedService("slave", 3306)
                .withOptions("--compatibility").withLocalCompose(true);
        CONTAINER.start();
    }

    @Autowired
    private ProductService productService;

    @DynamicPropertySource
    public static void initProperties(DynamicPropertyRegistry registry) throws SQLException {
        registry.add("master.datasource.jdbcUrl", () -> String.format(JDBC_URL_TEMPLATE, CONTAINER.getServiceHost("master", 3306), CONTAINER.getServicePort("master", 3306)));
        registry.add("master.datasource.username", () -> "root");
        registry.add("master.datasource.password", () -> "master");

        registry.add("slave.datasource.jdbcUrl", () -> String.format(JDBC_URL_TEMPLATE, CONTAINER.getServiceHost("slave", 3306), CONTAINER.getServicePort("slave", 3306)));
        registry.add("slave.datasource.username", () -> "root");
        registry.add("slave.datasource.password", () -> "slave");

        MASTER_CONNECTION = DriverManager.getConnection(String.format(JDBC_URL_TEMPLATE, CONTAINER.getServiceHost("master", 3306), CONTAINER.getServicePort("master", 3306)), "root", "master");
        SLAVE_CONNECTION = DriverManager.getConnection(String.format(JDBC_URL_TEMPLATE, CONTAINER.getServiceHost("slave", 3306), CONTAINER.getServicePort("slave", 3306)), "root", "slave");
    }

    @Test
    public void contextLoads() {

    }

    private ResultSet getAllMaster() throws SQLException {
        final var statement = MASTER_CONNECTION.createStatement();
        return statement.executeQuery("SELECT * FROM tests.product");
    }

    private ResultSet getAllSlave() throws SQLException {
        final var statement = SLAVE_CONNECTION.createStatement();
        return statement.executeQuery("SELECT * FROM tests.product");
    }

    @Test
    public void testService() throws SQLException, InterruptedException {
        var p = productService.create("test_0");

        assertThat(p).isNotNull();

        final var master = getAllMaster();

        assertThat(master.next()).isTrue();
        assertThat(master.getString("name")).isEqualTo("test_0");

        assertThat(productService.findByName("test_0")).isNull();

        Thread.sleep(5000);

        assertThat(productService.findByName("test_0")).isNotNull();
    }
}
