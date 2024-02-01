package dmx.springbootdb.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "comments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class CommentEntity {
    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    private UserEntity user;
    @NotBlank
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate;
}
