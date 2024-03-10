package dmx.springbootdb.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("customers")
public class Customer {
    @Id
    private String id;
    private int age;
    private int score;
    private String firstName;
    private String lastName;

    public Customer(int age, int score, String firstName, String lastName) {
        this.age = age;
        this.score = score;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
