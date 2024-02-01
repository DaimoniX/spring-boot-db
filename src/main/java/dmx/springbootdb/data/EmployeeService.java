package dmx.springbootdb.data;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeEntity findByName(String name) {
        return employeeRepository.findByName(name);
    }

    @PostConstruct
    private void init() {
        var admin = new EmployeeEntity();
        admin.setName("admin");
        admin.setEmail("admin@mail.com");
        admin.setSalary(1000);
        employeeRepository.save(admin);
    }
}
