package dmx.springbootdb.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "employees")
@EqualsAndHashCode(callSuper = true)
public class EmployeeEntity extends UserEntity {
    @NotNull
    @Min(0)
    private double salary;
}
