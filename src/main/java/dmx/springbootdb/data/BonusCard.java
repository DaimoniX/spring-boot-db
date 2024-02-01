package dmx.springbootdb.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "bonus_card_type", discriminatorType = DiscriminatorType.INTEGER)
public abstract class BonusCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "card_id")
    private int id;
    @Min(0)
    private double discount;
}
