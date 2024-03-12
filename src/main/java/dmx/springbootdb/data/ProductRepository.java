package dmx.springbootdb.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Product findFirstByName(String name);

    List<Product> findProductsByPriceLessThan(double price);

    void deleteProductByName(String name);
}
