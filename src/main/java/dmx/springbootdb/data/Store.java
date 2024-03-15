package dmx.springbootdb.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Store {
    @Id
    private String address;
    @Column(name = "store_name")
    private String name;
}
