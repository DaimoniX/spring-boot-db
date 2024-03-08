package dmx.springbootdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class IndexTest {
    private static Connection connection;

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @BeforeEach
    public void init() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void indexExists() throws SQLException {
        final var res = query("SHOW INDEX FROM product");
        final var indexes = new ArrayList<String>();
        while (res.next())
            indexes.add(res.getString("Key_name"));
        assertThat(indexes).contains("name_index", "complex_index", "unique_name_index");
    }


    private static ResultSet query(String query) throws SQLException {
        final var statement = connection.createStatement();
        return statement.executeQuery(query);
    }
}
