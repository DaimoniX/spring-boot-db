package dmx.springbootdb;

import dmx.springbootdb.data.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase
public class AppDataJpaTest {
    @Autowired
    private EmployeeRepository employeeRepository;

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
    public void repoTestError() {
        final var user = new EmployeeEntity();
        user.setName("");
        user.setEmail("error");
        assertThatThrownBy(() -> employeeRepository.saveAndFlush(user)).isInstanceOf(ConstraintViolationException.class);
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
