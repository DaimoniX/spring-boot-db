package dmx.springbootdb;

import dmx.springbootdb.emanager.TestEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "spring.jpa.hibernate.ddl-auto=validate" })
public class CriteriaTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<TestEntity> entityQuery;

    protected void save(TestEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    @BeforeAll
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        save(new TestEntity("T1", 1));
        save(new TestEntity("T2", 2));
        save(new TestEntity("T3", 3));
        save(new TestEntity("T4", 4));
        save(new TestEntity("T5", 5));
        criteriaBuilder = entityManager.getCriteriaBuilder();
        entityQuery = criteriaBuilder.createQuery(TestEntity.class);
    }

    @AfterAll
    public void cleanUp() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("delete from TestEntity").executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Test
    public void criteriaTest() {
        final var selectAllQuery = entityQuery.select(entityQuery.from(TestEntity.class));
        final var result = entityManager.createQuery(selectAllQuery).getResultList();
        assertThat(result).asList().hasSize(5);
    }

    @Test
    public void criteriaTestByValue() {
        final var selectWhereQuery = entityQuery.where(criteriaBuilder.equal(entityQuery.from(TestEntity.class).get("id"), 3));
        final var result = entityManager.createQuery(selectWhereQuery).getResultList();
        assertThat(result).asList().hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3);
    }

    @Test
    public void criteriaTestSort() {
        final var em = entityManagerFactory.createEntityManager();
        final var cb = em.getCriteriaBuilder();
        final var eq = cb.createQuery(TestEntity.class);

        final var root = eq.from(TestEntity.class);
        final var order = cb.desc(root.get("id"));
        final var selectSortQuery = eq.orderBy(order);
        final var result = em.createQuery(selectSortQuery).getResultList();

        assertThat(result).asList().extracting("id").containsExactly(5, 4, 3, 2, 1);
    }

    // gt = greater than
    // lt = less than
    // ge = greater than or equal to
    // le = less than or equal to
    @Test
    public void criteriaTestAnd() {
        final var root = entityQuery.from(TestEntity.class);
        final var predicates = new Predicate[2];
        predicates[0] = criteriaBuilder.ge(root.get("id"), 3);
        predicates[1] = criteriaBuilder.lt(root.get("id"), 5);

        final var selectAndQuery = entityQuery.select(root).where(predicates);
        final var result = entityManager.createQuery(selectAndQuery).getResultList();
        assertThat(result).asList().hasSize(2);
    }
}
