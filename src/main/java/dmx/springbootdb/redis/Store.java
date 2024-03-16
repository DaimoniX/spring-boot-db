package dmx.springbootdb.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("Store")
public class Store implements Serializable {
    @Id
    private String address;
    private String name;
}
