package dmx.springbootdb;


import dmx.springbootdb.data.Author;
import dmx.springbootdb.data.AuthorRepository;
import dmx.springbootdb.data.Book;
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
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class JSONTest {
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
    public void testConnection(){
        assertThat(MY_SQL_CONTAINER.isRunning()).isTrue();
    }

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
    }

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void testAuthorRepository(){
        assertThat(authorRepository).isNotNull();

        final var book = Book.builder().title("The Hobbit").isbn("1234").price(20).build();
        final var author = Author.builder().name("Name").email("mail@mail.com").book(book).build();

        authorRepository.saveAndFlush(author);

        assertThat(authorRepository.findAll()).isNotEmpty();
        assertThat(authorRepository.findAll().get(0).getBook()).isEqualTo(book);

        assertThat(authorRepository.findByBookTitle("The Hobbit")).isEqualTo(author);
    }
}

