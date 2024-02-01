package dmx.springbootdb.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@Entity
@Table(name = "customers")
@EqualsAndHashCode(callSuper = true)
public class CustomerEntity extends UserEntity {
    @Min(0)
    private int score;
    @Min(12)
    @Max(12)
    @UniqueElements
    private String card;
}
