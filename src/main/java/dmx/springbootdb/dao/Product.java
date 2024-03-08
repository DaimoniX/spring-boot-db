package dmx.springbootdb.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "product", indexes = {
        @Index(name = "name_index", columnList = "name"),
        @Index(name = "complex_index", columnList = "name, val"),
        @Index(name = "unique_name_index", columnList = "name", unique = true)
})
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String name;
    private int val;
    private double weight;

    public Product(String name, int val) {
        this.name = name;
        this.val = val;
    }
}
