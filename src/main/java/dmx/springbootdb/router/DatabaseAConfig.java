package dmx.springbootdb.router;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix="db-a.datasource")
public class DatabaseAConfig {
    private String url;
    private String password;
    private String username;
}
