package dmx.springbootdb.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;

@Converter
public class BookConverter implements AttributeConverter<Book, String> {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writer();

    @Override
    @SneakyThrows
    public String convertToDatabaseColumn(Book book) {
        return writer.writeValueAsString(book);
    }

    @Override
    @SneakyThrows
    public Book convertToEntityAttribute(String string) {
        return mapper.readValue(string, Book.class);
    }
}
