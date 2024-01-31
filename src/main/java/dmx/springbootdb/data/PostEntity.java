package dmx.springbootdb.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "posts")
public class PostEntity {
    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false)
    @NotBlank
    @Min(4)
    private String title;
    @Column(nullable = false)
    @NotBlank
    @Min(64)
    private String content;
    @OneToMany(fetch = FetchType.EAGER)
    @OrderBy("postedDate DESC")
    private List<CommentEntity> comments;
}
