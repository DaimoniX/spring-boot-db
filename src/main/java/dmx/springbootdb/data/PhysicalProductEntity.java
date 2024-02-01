package dmx.springbootdb.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class PhysicalProductEntity extends ProductEntity {
    @Min(0)
    private int weight;
}
