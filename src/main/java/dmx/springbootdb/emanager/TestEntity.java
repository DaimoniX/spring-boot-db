package dmx.springbootdb.emanager;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class TestEntity {
    @Id
    private int id;
    @NotBlank
    private String name;
}
