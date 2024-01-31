package dmx.springbootdb;

import dmx.springbootdb.data.UserEntity;
import dmx.springbootdb.data.UserRepository;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class AppDataJpaTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void userRepositoryIsNotNull(){
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void repoTest() {
        final var user = new UserEntity();
        user.setName("admin");
        user.setEmail("admin@mail.com");
        userRepository.save(user);
        assertThat(userRepository.findByName("admin")).isNotNull();
        assertThat(userRepository.findByName("not_found")).isNull();
    }

    @Test
    public void validationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final var validator = factory.getValidator();

        final var badUser = new UserEntity();
        badUser.setName("");
        badUser.setEmail("hello");
        var violations = validator.validate(badUser);
        assertThat(violations.size()).isEqualTo(2);

        final var user = new UserEntity();
        user.setName("admin");
        user.setEmail("admin@mail.com");
        violations = validator.validate(user);
        assertThat(violations.size()).isEqualTo(0);
    }
}
