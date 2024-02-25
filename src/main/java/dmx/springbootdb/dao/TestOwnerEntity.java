package dmx.springbootdb.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "test_owner_entity")
@Entity
@NoArgsConstructor
public class TestOwnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "test_entity_id", referencedColumnName = "id")
    private TestEntity testEntity;
}
