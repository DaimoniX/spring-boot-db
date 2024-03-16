package dmx.springbootdb.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
@Builder
@RedisHash("Product")
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    @Id
    private String id;
    @Indexed
    private String name;
    @Indexed
    private long price;
    @Indexed
    private int stock;
}
