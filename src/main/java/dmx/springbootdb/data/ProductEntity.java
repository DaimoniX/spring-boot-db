package dmx.springbootdb.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProductEntity {
    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private int id;
    @Column(nullable = false)
    @NotBlank
    private String name;
    @Column(nullable = false)
    private String description;
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @OrderBy("postedDate ASC")
    private List<CommentEntity> comments;
    @Min(0)
    private double price;

    @OrderBy("id ASC")
    public List<CommentEntity> getComments() {
        return comments;
    }
}
