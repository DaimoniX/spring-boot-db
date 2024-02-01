package dmx.springbootdb;

import dmx.springbootdb.data.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Date;

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
        registry.add("spring.jpa.hibernate.ddl-auto",() -> "create-drop");
    }

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DigitalProductRepository productRepository;

    @Test
    public void repoTest() {
        final var user = new EmployeeEntity();
        user.setName("test");
        user.setEmail("test@mail.com");
        user.setSalary(10);
        employeeRepository.save(user);
        assertThat(employeeRepository.findByName("test")).isNotNull();
        assertThat(employeeRepository.findByName("not_found")).isNull();
    }

    @Test
    public void serviceTest() {
        assertThat(employeeService.findByName("admin")).isNotNull();
    }

    @Test
    @Sql(scripts = "classpath:test.sql")
    public void sqlTest() {
        assertThat(employeeRepository.findByName("sql-user")).isNotNull();
    }

    @Test
    public void orderByTest() {
        final var comments = new ArrayList<CommentEntity>();
        final var today = new Date();

        final var comment1 = new CommentEntity();
        comment1.setUser(null);
        comment1.setPostedDate(new Date(today.getTime() - 24 * 60 * 60 * 1000));
        comment1.setContent("c1");
        final var comment2 = new CommentEntity();
        comment2.setUser(null);
        comment2.setPostedDate(new Date(today.getTime() - 24 * 30 * 60 * 1000));
        comment2.setContent("c2");
        final var comment3 = new CommentEntity();
        comment3.setUser(null);
        comment3.setPostedDate(new Date(today.getTime() - 24 * 45 * 60 * 1000));
        comment3.setContent("c3");

        comments.add(comment1);
        comments.add(comment2);
        comments.add(comment3);

        final var product = new DigitalProductEntity();
        product.setPrice(10);
        product.setName("product1");
        product.setUrl("https://example.com");
        product.setDescription("description");
        product.setComments(comments);
        productRepository.save(product);
        productRepository.flush();
        final var rp = productRepository.findByName("product1");
        assertThat(rp).isNotNull();
        final var rCom = rp.getComments();
        assertThat(rCom.get(0).getContent()).isEqualTo("c1");
        assertThat(rCom.size()).isEqualTo(3);

        assertThat(rCom.get(0).getPostedDate().getTime()).isLessThan(rCom.get(1).getPostedDate().getTime());
        assertThat(rCom.get(1).getPostedDate().getTime()).isLessThan(rCom.get(2).getPostedDate().getTime());
    }
}
