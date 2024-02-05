package dmx.springbootdb.emanager;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestEntityRepository extends JpaRepository<TestEntity, Integer> {
    TestEntity findByName(String name);
}
