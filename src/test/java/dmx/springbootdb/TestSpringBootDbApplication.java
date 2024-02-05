package dmx.springbootdb;

import dmx.springbootdb.emanager.TestEntity;
import dmx.springbootdb.emanager.TestEntityRepository;
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
public class TestSpringBootDbApplication {
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
    private TestEntityRepository testEntityRepository;

    @Test
    public void testRepository() {
        final var entity = new TestEntity();
        entity.setName("testRepository");
        testEntityRepository.save(entity);
        final var e = testEntityRepository.findByName("testRepository");
        assertThat(e).isNotNull();
        assertThat(e.getName()).isEqualTo(entity.getName());
    }
}
