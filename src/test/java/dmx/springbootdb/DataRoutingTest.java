package dmx.springbootdb;

import dmx.springbootdb.dao.TestEntityService;
import dmx.springbootdb.router.DatabaseContextHolder;
import dmx.springbootdb.router.DatabaseEnum;
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
public class DataRoutingTest {
    @Container
    @ServiceConnection
    private static final MySQLContainer<?> MY_SQL_CONTAINER_A = new MySQLContainer<>(DockerImageName.parse("mysql:8.3"));
    @Container
    @ServiceConnection
    private static final MySQLContainer<?> MY_SQL_CONTAINER_B = new MySQLContainer<>(DockerImageName.parse("mysql:8.3"));

    @BeforeAll
    public static void beforeAll() {
        MY_SQL_CONTAINER_A.start();
        MY_SQL_CONTAINER_B.start();
    }

    @AfterAll
    public static void afterAll() {
        MY_SQL_CONTAINER_A.stop();
        MY_SQL_CONTAINER_B.stop();
    }

    @DynamicPropertySource
    public static void configureTestProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("db-a.datasource.url", MY_SQL_CONTAINER_A::getJdbcUrl);
        registry.add("db-a.datasource.username", MY_SQL_CONTAINER_A::getUsername);
        registry.add("db-a.datasource.password", MY_SQL_CONTAINER_A::getPassword);
        registry.add("db-a.jpa.hibernate.ddl-auto", () -> "create");

        registry.add("db-b.datasource.url", MY_SQL_CONTAINER_B::getJdbcUrl);
        registry.add("db-b.datasource.username", MY_SQL_CONTAINER_B::getUsername);
        registry.add("db-b.datasource.password", MY_SQL_CONTAINER_B::getPassword);
        registry.add("db-b.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Test
    public void containerStatusTest() {
        assertThat(MY_SQL_CONTAINER_A.isCreated()).isTrue();
        assertThat(MY_SQL_CONTAINER_B.isCreated()).isTrue();
        assertThat(MY_SQL_CONTAINER_A.isRunning()).isTrue();
        assertThat(MY_SQL_CONTAINER_B.isRunning()).isTrue();
    }

    @Autowired
    private DatabaseContextHolder databaseContextHolder;
    @Autowired
    private TestEntityService testEntityService;

    @Test
    public void test() {
        databaseContextHolder.setContext(DatabaseEnum.DATABASE_A);
        assertThat(databaseContextHolder.getContext()).isEqualTo(DatabaseEnum.DATABASE_A);

        final var e = testEntityService.create("A");
        assertThat(e).isNotNull();
        assertThat(testEntityService.findByName("A")).isNotNull();

        databaseContextHolder.setContext(DatabaseEnum.DATABASE_B);
        assertThat(databaseContextHolder.getContext()).isEqualTo(DatabaseEnum.DATABASE_B);

        final var eb = testEntityService.create("B");
        assertThat(eb).isNotNull();
        assertThat(testEntityService.findByName("B")).isNotNull();

        assertThat(testEntityService.findByName("A")).isNull();
    }
}
