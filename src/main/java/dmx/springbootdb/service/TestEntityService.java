package dmx.springbootdb.service;

import dmx.springbootdb.dao.TestEntity;
import dmx.springbootdb.dao.TestEntityRepository;
import dmx.springbootdb.router.DatabaseContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestEntityService {
    private final DatabaseContextHolder databaseContextHolder;
    private final TestEntityRepository testEntityRepository;

    public TestEntity create(String name) {
        setDatabase(DBEnum.MASTER);
        TestEntity testEntity = new TestEntity();
        testEntity.setName(name);
        return testEntityRepository.save(testEntity);
    }

    private void setDatabase(DBEnum db) {
        databaseContextHolder.setContext(DBEnum.getDatabaseEnum(db));
    }

    public List<TestEntity> findAll() {
        setDatabase(DBEnum.SLAVE);
        return testEntityRepository.findAll();
    }

    public TestEntity findByName(String name) {
        setDatabase(DBEnum.SLAVE);
        return testEntityRepository.findByName(name);
    }

    public TestEntity findByNameWithDB(String name, DBEnum db) {
        setDatabase(db);
        return testEntityRepository.findByName(name);
    }

    public void replicate() {
        setDatabase(DBEnum.MASTER);
        final var masterEntities = testEntityRepository.findAll();
        setDatabase(DBEnum.SLAVE);
        testEntityRepository.deleteAll();
        testEntityRepository.saveAll(masterEntities);
        testEntityRepository.flush();
    }
}
