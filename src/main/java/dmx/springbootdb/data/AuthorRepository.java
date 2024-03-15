package dmx.springbootdb.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Author findByName(String name);

    Author findByEmail(String email);

    @Query(value = "SELECT * FROM `author` WHERE `book`->'$.title' = :title", nativeQuery = true)
    Author findByBookTitle(String title);
}
