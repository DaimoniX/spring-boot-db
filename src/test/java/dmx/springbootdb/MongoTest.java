package dmx.springbootdb;

import dmx.springbootdb.data.Customer;
import dmx.springbootdb.data.CustomerRepository;
import dmx.springbootdb.data.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class MongoTest {
    @ServiceConnection
    private static final MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:4.0.10");

    @BeforeAll
    public static void setUp(){
        mongoDbContainer.start();
    }

    @Test
    public void testConnection(){
        assertThat(mongoDbContainer.isRunning()).isTrue();
    }

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.mongodb.uri", mongoDbContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void serviceTest(){
        final var product = productService.createProduct("Test Product", 100.0);
        assertThat(productService.getProducts()).contains(product);
        assertThat(productService.updatePriceById(product.getId(), 55).getPrice()).isEqualTo(55);
        productService.deleteProduct(product.getId());
        assertThat(productService.getProducts()).doesNotContain(product);
    }

    @Test
    public void dslTest(){
        final var customer = customerRepository.save(new Customer(20, 100, "John", "Doe"));
        assertThat(customerRepository.findAll()).contains(customer);
        assertThat(customerRepository.findByFirstName("John")).isEqualTo(customer);
        customerRepository.findAndIncrementScoreByFirstName("John");
        assertThat(customerRepository.findByFirstName("John").getScore()).isEqualTo(101);
        customerRepository.save(new Customer(20, 200, "Jane", "Doe"));
        assertThat(customerRepository.findAllWithScoreGreaterThan(100)).hasSize(2);
    }
}
