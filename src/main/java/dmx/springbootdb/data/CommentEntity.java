package dmx.springbootdb.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "comments")
public class CommentEntity {
    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private int id;
    @OneToOne
    private UserEntity user;
    @NotBlank
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate;
}
