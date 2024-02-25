package dmx.springbootdb.dao;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionTestManager {
    private EntityManager entityManager;
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    private void init() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    public TestEntity insert(TestEntity entity) {
        startTransaction();
        entityManager.persist(entity);
        completeTransaction();
        return entity;
    }

    public void insertNonTransactional(TestEntity entity) {
        entityManager.persist(entity);
    }

    public void flush() {
        entityManager.flush();
    }

    public void detach(TestEntity entity) {
        entityManager.detach(entity);
    }

    @Transactional
    public void merge(TestEntity entity) {
        entityManager.merge(entity);
    }

    public void startTransaction() {
        entityManager.getTransaction().begin();
    }

    public void completeTransaction() {
        entityManager.getTransaction().commit();
    }

    public void removeById(int id) {
        final var entity = findById(id);
        if (entity == null) return;
        startTransaction();
        entityManager.remove(entity);
        completeTransaction();
    }

    public void refresh(TestEntity entity) {
        startTransaction();
        entityManager.refresh(entity);
        completeTransaction();
    }

    public TestEntity findById(int id) {
        return entityManager.find(TestEntity.class, id);
    }

    public List<TestEntity> findAll() {
        return entityManager.createQuery("SELECT e FROM TestEntity e", TestEntity.class).setMaxResults(10).getResultList();
    }

    public TestEntity findByName(String name) {
        return entityManager.createQuery("SELECT e FROM TestEntity e WHERE e.name = :name", TestEntity.class).setParameter("name", name).getSingleResult();
    }
}
