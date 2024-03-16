package dmx.springbootdb;


import com.redis.testcontainers.RedisContainer;
import dmx.springbootdb.redis.Product;
import dmx.springbootdb.redis.ProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class RedisTest {
    @Container
    @ServiceConnection
    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

    @BeforeAll
    public static void beforeAll() {
        REDIS_CONTAINER.start();
    }

    @AfterAll
    public static void afterAll() {
        REDIS_CONTAINER.stop();
    }

    @Test
    public void testConnection(){
        assertThat(REDIS_CONTAINER.isRunning()).isTrue();
    }

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.redis.url", REDIS_CONTAINER::getRedisURI);
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testAuthorRepository(){
        assertThat(productRepository).isNotNull();

        final var filledProducts = fillProducts(productRepository);

        assertThat(productRepository.findAll()).hasSize(10);
        assertThat(productRepository.findAll()).containsAll(filledProducts);

        assertThat(productRepository.findById(filledProducts.get(0).getId())).isPresent();

        assertThat(productRepository.findByPrice(filledProducts.get(0).getPrice())).containsExactly(filledProducts.get(0));

        assertThat(productRepository.findByStock(filledProducts.get(0).getStock())).containsExactly(filledProducts.get(0));

        assertThat(productRepository.findByName(filledProducts.get(0).getName())).isEqualTo(filledProducts.get(0));

        assertThat(productRepository.findFirstByNameOrderByPrice(filledProducts.get(0).getName())).isEqualTo(filledProducts.get(0));
    }

    private static List<Product> fillProducts(ProductRepository productRepository) {
        final var products = new ArrayList<Product>();
        for (int i = 0; i < 10; i++) {
            final var product = Product.builder()
                    .name("Product " + i)
                    .price(i * 100L)
                    .stock(i * 10)
                    .build();

            products.add(productRepository.save(product));
        }
        return products;
    }
}

