package dmx.springbootdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class SpringBootDbApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDbApplication.class, args);
    }
}
