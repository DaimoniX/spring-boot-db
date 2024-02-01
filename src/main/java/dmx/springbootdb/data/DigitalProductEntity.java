package dmx.springbootdb.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class DigitalProductEntity extends ProductEntity {
    @NotBlank
    private String url;
}
