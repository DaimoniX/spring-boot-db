package dmx.springbootdb.data;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EmployeeBonusCardEntity extends BonusCard {
    @NotNull
    @OneToOne
    private EmployeeEntity owner;
}
