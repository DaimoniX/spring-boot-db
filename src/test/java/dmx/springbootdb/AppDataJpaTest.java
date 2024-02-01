package dmx.springbootdb;

import dmx.springbootdb.data.*;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class AppDataJpaTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DigitalProductRepository productRepository;

    @Test
    public void userRepositoryIsNotNull(){
        assertThat(employeeRepository).isNotNull();
    }

    @Test
    public void repoTest() {
        final var user = new EmployeeEntity();
        user.setName("admin");
        user.setEmail("admin@mail.com");
        employeeRepository.saveAndFlush(user);
        assertThat(employeeRepository.findByName("admin")).isNotNull();
        assertThat(employeeRepository.findByName("not_found")).isNull();
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
        final var rp = productRepository.findById(1);
        assertThat(rp.isPresent()).isTrue();
        final var val = rp.get();
        final var rCom = val.getComments();
        assertThat(rCom.get(0).getContent()).isEqualTo("c1");
        assertThat(rCom.size()).isEqualTo(3);

        System.out.println(rCom);

        // check if rCom is sorted by postedDate
//        assertThat(rCom.get(0).getPostedDate().getTime()).isLessThan(rCom.get(1).getPostedDate().getTime());
//        assertThat(rCom.get(1).getPostedDate().getTime()).isLessThan(rCom.get(2).getPostedDate().getTime());
    }

    @Test
    public void validationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final var validator = factory.getValidator();

        final var badUser = new EmployeeEntity();
        badUser.setName("");
        badUser.setEmail("hello");
        badUser.setSalary(-1);
        var violations = validator.validate(badUser);
        assertThat(violations.size()).isEqualTo(3);

        final var user = new EmployeeEntity();
        user.setName("admin");
        user.setEmail("admin@mail.com");
        user.setSalary(5);
        violations = validator.validate(user);
        assertThat(violations.size()).isEqualTo(0);
    }
}
