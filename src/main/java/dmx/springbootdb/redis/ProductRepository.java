package dmx.springbootdb.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {
    List<Product> findByPrice(long price);

    Product findByName(String name);

    Product findFirstByNameOrderByPrice(String name);

    List<Product> findByStock(int stock);
}
