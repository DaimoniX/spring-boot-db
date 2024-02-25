package dmx.springbootdb.tran;

import dmx.springbootdb.dao.TestEntity;
import dmx.springbootdb.dao.TestEntityRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
public class TransTestManager {
    private final TestEntityRepository testEntityRepository;
    private final PlatformTransactionManager transactionManager;
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Transactional
    public void addEntityTransactional(String name, boolean fail) {
        testEntityRepository.save(new TestEntity(name, 55));
        if (fail)
            throw new RuntimeException("Failed");
    }

    public TestEntity addEntityTransactionTemplate(String name, boolean fail) {
        final var template = new TransactionTemplate(transactionManager);
        return template.execute(status -> {
            final var entity = testEntityRepository.save(new TestEntity(name, 555));
            if (fail)
                throw new RuntimeException("Failed");
            return entity;
        });
    }

    public void addEntityEntityManager(String name, boolean fail) {
        final var entity = new TestEntity(name, 5555);
        final var entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            entityManager.persist(entity);

            if (fail)
                throw new RuntimeException("Failed");

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public TestEntity getWithName(String name) {
        return testEntityRepository.findByName(name);
    }
}
