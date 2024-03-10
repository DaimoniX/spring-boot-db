package dmx.springbootdb.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document("receipts")
public class Receipt {
    @Id
    private String id;
    private double total;
    private Instant date;
}
