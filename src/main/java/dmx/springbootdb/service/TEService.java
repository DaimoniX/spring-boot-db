package dmx.springbootdb.service;

import dmx.springbootdb.dao.TestEntity;
import dmx.springbootdb.dao.TestEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class TEService {
    private final TestEntityRepository testEntityRepository;

    private static void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            System.out.println("Interrupted" + e);
        }
    }

    public TestEntity getWithName(String name) {
        return testEntityRepository.findByName(name);
    }

    public List<TestEntity> getAll() {
        return testEntityRepository.findAll();
    }

    public void setEntityValue(String name, int val) {
        var entity = testEntityRepository.findByName(name);
        entity.setVal(val);
        testEntityRepository.save(entity);
        testEntityRepository.flush();
    }

    // Propagations
    @Transactional(propagation = Propagation.REQUIRED)
    public void addEntityTransactionalRequired(String name, boolean fail) {
        testEntityRepository.save(new TestEntity(name, 55));
        if (fail) throw new RuntimeException("Failed");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addEntityTransactionalRequiresNew(String name, boolean fail) {
        testEntityRepository.save(new TestEntity(name, 55));
        if (fail) throw new RuntimeException("Failed");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addEntityTransactionalRequiredAndRequiresNew(String name) {
        addEntityTransactionalRequired(name + "-req", false);
        addEntityTransactionalRequiresNew(name + "req_new", true);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addEntityTransactionalMandatory(String name) {
        testEntityRepository.save(new TestEntity(name, 55));
    }

    @Transactional(propagation = Propagation.NEVER)
    public void addEntityTransactionalNever(String name) {
        testEntityRepository.save(new TestEntity(name, 55));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addEntityTransactionalNotSupported(String name, boolean fail) {
        testEntityRepository.save(new TestEntity(name, 55));
        if (fail) throw new RuntimeException("Failed");
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void addEntityTransactionalSupports(String name, boolean fail) {
        testEntityRepository.save(new TestEntity(name, 55));
        if (fail) throw new RuntimeException("Failed");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addEntityTransactionalSupportsWrapper(String name, boolean fail) {
        addEntityTransactionalSupports(name, fail);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void addEntityTransactionalNested(String name) {
        testEntityRepository.save(new TestEntity(name, 55));
    }


    // Isolation Values
    @Transactional(isolation = Isolation.DEFAULT)
    public Map.Entry<Integer, Integer> getEntityValuesDefault(String name) {
        var val1 = testEntityRepository.findByName(name + "1").getVal();
        var val2 = testEntityRepository.findByName(name + "2").getVal();
        return Map.entry(val1, val2);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public synchronized Map.Entry<Integer, Integer> getEntityValuesReadUncommitted(String name) {
        var val1 = testEntityRepository.findByName(name + "1").getVal();
        var val2 = testEntityRepository.findByName(name + "2").getVal();
        return Map.entry(val1, val2);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map.Entry<Integer, Integer> getEntityValuesCommitted(String name) {
        var val1 = testEntityRepository.findByName(name + "1").getVal();
        var val2 = testEntityRepository.findByName(name + "2").getVal();
        return Map.entry(val1, val2);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map.Entry<Integer, Integer> getEntityValuesRepeatableRead(String name, int delay) {
        int val1 = testEntityRepository.findByName(name + "1").getVal();
        sleep(delay);
        int val2 = testEntityRepository.findByName(name + "1").getVal();
        return Map.entry(val1, val2);
    }

    // Isolation set value
    @Transactional(isolation = Isolation.DEFAULT)
    public void setEntityValuesDefault(String name, int val) {
        setEntityValue(name + "1", val);
        sleep(1000);
        setEntityValue(name + "2", val + 1000);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void setEntityValuesReadUncommitted(String name, int val) {
        setEntityValue(name + "1", val);
        sleep(1000);
        setEntityValue(name + "2", val + 1000);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setEntityValuesCommitted(String name, int val) {
        setEntityValue(name + "1", val);
        sleep(1000);
        setEntityValue(name + "2", val + 1000);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void setEntityValuesRepeatableRead(String name, int val) {
        setEntityValue(name + "1", val);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void setEntityValuesSerializable(String name, int val, Consumer<Integer> firstCallback, Consumer<Integer> secondCallback) {
        setEntityValue(name + "1", val);
        firstCallback.accept(val);
        sleep(5000);
        setEntityValue(name + "2", val + 500);
        secondCallback.accept(val + 500);
    }
}
