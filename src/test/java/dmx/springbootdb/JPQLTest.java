package dmx.springbootdb;

import dmx.springbootdb.dao.TestEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "spring.jpa.hibernate.ddl-auto=validate" })
public class JPQLTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    protected void save(TestEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    @BeforeEach
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        save(new TestEntity("T1", 1));
        save(new TestEntity("T2", 2));
        save(new TestEntity("T3", 3));
        save(new TestEntity("T4", 4));
        save(new TestEntity("T5", 5));
    }

    @AfterEach
    public void cleanUp() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("delete from TestEntity").executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Test
    public void test() {
        final var query = entityManager.createQuery("select (e.val) from TestEntity e");
        final var result = query.getResultList();
        assertThat(result).asList().containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void test2() {
        final var query = entityManager.createQuery("select (e.name) from TestEntity e where e.val = 3", String.class);
        final var result = query.getSingleResult();
        assertThat(result).isEqualTo("T3");
    }

    @Test
    public void testUpdateQuery() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("update TestEntity e set e.name = 'T6'").executeUpdate();
        entityManager.getTransaction().commit();
        final var result = entityManager.createQuery("select (e.name) from TestEntity e", String.class).getResultList();
        assertThat(result).asList().containsExactly("T6", "T6", "T6", "T6", "T6");
    }

    @Test
    public void testDeleteQuery() {
        final var query = entityManager.createQuery("select (e.val) from TestEntity e");
        var result = query.getResultList();
        assertThat(result).asList().isNotEmpty();

        entityManager.getTransaction().begin();
        entityManager.createQuery("delete from TestEntity").executeUpdate();
        entityManager.getTransaction().commit();

        result = query.getResultList();
        assertThat(result).asList().isEmpty();
    }

    @Test
    public void testAggregateQuery() {
        var query = entityManager.createQuery("select count(e) from TestEntity e");
        var result = query.getSingleResult();
        assertThat(result).isEqualTo(5L);

        query = entityManager.createQuery("select sum(e.val) from TestEntity e");
        result = query.getSingleResult();
        assertThat(result).isEqualTo(15L);

        query = entityManager.createQuery("select avg(e.val) from TestEntity e");
        result = query.getSingleResult();
        assertThat(result).isEqualTo(3.0);
    }
}
