package dmx.springbootdb;

import dmx.springbootdb.tran.TransTestManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionTest {
    @Container
    @ServiceConnection
    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.3"));

    @BeforeAll
    public static void beforeAll() {
        MY_SQL_CONTAINER.start();
    }

    @AfterAll
    public static void afterAll() {
        MY_SQL_CONTAINER.stop();
    }

    @Test
    public void containerStatusTest() {
        assertThat(MY_SQL_CONTAINER.isCreated()).isTrue();
        assertThat(MY_SQL_CONTAINER.isRunning()).isTrue();
    }

    @DynamicPropertySource
    public static void configureTestProperties(@NotNull DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto",() -> "validate");
    }

    @Autowired
    private TransTestManager transTestManager;

    @Test
    public void annotationTest() {
        transTestManager.addEntityTransactional("ant1", false);
        assertThat(transTestManager.getWithName("ant1")).isNotNull();

        try {
            transTestManager.addEntityTransactional("ant2", true);
        } catch (Exception e) {
            System.out.println("EXC");
        }

        assertThat(transTestManager.getWithName("ant2")).isNull();
    }

    @Test
    public void templateTest() {
        transTestManager.addEntityTransactionTemplate("tmp1", false);
        assertThat(transTestManager.getWithName("tmp1")).isNotNull();

        try {
            transTestManager.addEntityTransactionTemplate("tmp2", true);
        } catch (Exception e) {
            System.out.println("EXC");
        }

        assertThat(transTestManager.getWithName("tmp2")).isNull();
    }

    @Test
    public void entityManagerTest() {
        transTestManager.addEntityEntityManager("em1", false);
        assertThat(transTestManager.getWithName("em1")).isNotNull();

        try {
            transTestManager.addEntityEntityManager("em2", true);
        } catch (Exception e) {
            System.out.println("EXC");
        }

        assertThat(transTestManager.getWithName("em2")).isNull();
    }
}
