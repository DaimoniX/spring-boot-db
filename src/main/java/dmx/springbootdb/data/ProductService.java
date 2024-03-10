package dmx.springbootdb.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product createProduct(String name, double price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return productRepository.save(product);
    }

    public Product updatePriceById(String id, double price) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            product.setPrice(price);
            return productRepository.save(product);
        }
        return null;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
