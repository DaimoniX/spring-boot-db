package dmx.springbootdb.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestEntityService {
    private final TestEntityRepository testEntityRepository;

    public void save(TestEntity testEntity) {
        testEntityRepository.save(testEntity);
    }

    public TestEntity create(String name) {
        TestEntity testEntity = new TestEntity();
        testEntity.setName(name);
        return testEntityRepository.save(testEntity);
    }

    public TestEntity findById(int id) {
        return testEntityRepository.findById(id).orElse(null);
    }

    public List<TestEntity> findAll() {
        return testEntityRepository.findAll();
    }

    public TestEntity findByName(String name) {
        return testEntityRepository.findByName(name);
    }
}
