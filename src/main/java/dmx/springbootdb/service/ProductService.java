package dmx.springbootdb.service;

import dmx.springbootdb.dao.Product;
import dmx.springbootdb.dao.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository testEntityRepository;

    public Product create(String name) {
        Product product = new Product();
        product.setName(name);
        return testEntityRepository.save(product);
    }

    public Product create(String name, int val) {
        Product product = new Product();
        product.setName(name);
        product.setVal(val);
        return testEntityRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return testEntityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findByName(String name) {
        return testEntityRepository.findByName(name);
    }
}
