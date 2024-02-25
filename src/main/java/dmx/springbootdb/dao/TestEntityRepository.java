package dmx.springbootdb.dao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestEntityRepository extends JpaRepository<TestEntity, Integer> {
    TestEntity findByName(String name);
}
