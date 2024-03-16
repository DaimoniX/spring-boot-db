package dmx.springbootdb.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("Worker")
public class Worker implements Serializable {
    @Id
    private String id;
    private String name;
    private long salary;
}
