package dmx.springbootdb.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "test_entity")
@Entity
@NoArgsConstructor
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String name;
    private int val;

    public TestEntity(String name, int val) {
        this.name = name;
        this.val = val;
    }
}
