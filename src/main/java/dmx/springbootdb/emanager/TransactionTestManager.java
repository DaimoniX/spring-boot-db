package dmx.springbootdb.emanager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class TransactionTestManager {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public TestEntity insert(TestEntity entity) {
        entityManager.persist(entity);
        return entity;
    }

    public TestEntity findById(int id) {
        return entityManager.find(TestEntity.class, id);
    }

    public TestEntity findByName(String name) {
        return entityManager.createQuery("SELECT e FROM TestEntity e WHERE e.name = :name", TestEntity.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}
