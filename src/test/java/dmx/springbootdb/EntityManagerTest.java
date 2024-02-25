package dmx.springbootdb;

import dmx.springbootdb.dao.TestEntity;
import dmx.springbootdb.dao.TransactionTestManager;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "spring.jpa.hibernate.ddl-auto=validate" })
public class EntityManagerTest {
    @Autowired
    private TransactionTestManager testManager;

    @Test
    public void insertTest() {
        final var entity = new TestEntity();
        entity.setName("insertTest");
        testManager.insert(entity);
        final var e = testManager.findById(entity.getId());
        assertThat(e).isNotNull();
        assertThat(entity.getId()).isEqualTo(e.getId());
        assertThat(entity.getName()).isEqualTo(e.getName());
        entity.setName("insertUpd");
        assertThat(testManager.findById(entity.getId()).getName()).isEqualTo(entity.getName());
    }

    @Test
    public void findTest() {
        final var entity = new TestEntity();
        entity.setName("findTest");
        final var e = testManager.insert(entity);
        assertThat(testManager.findById(e.getId())).isNotNull();
        assertThat(testManager.findByName("findTest")).isNotNull();
        assertThat(testManager.findAll()).isNotEmpty();
    }

    @Test
    public void insertNonTransactionalTest() {
        final var entity = new TestEntity();
        entity.setName("INT Test");
        testManager.startTransaction();
        testManager.insertNonTransactional(entity);
        testManager.flush();
        testManager.completeTransaction();
        assertThat(testManager.findByName("INT Test")).isNotNull();
    }

    @Test
    public void detachTest() {
        final var entity = new TestEntity();
        entity.setName("Test");
        testManager.startTransaction();
        testManager.insertNonTransactional(entity);
        entity.setName("Test Insert");
        testManager.flush();
        testManager.detach(entity);
        entity.setName("Not Insert");
        testManager.completeTransaction();
        assertThat(testManager.findByName("Test Insert")).isNotNull();
        assertThatThrownBy(() -> testManager.findByName("Not Insert")).isInstanceOf(NoResultException.class);
    }

    @Test
    public void detachTest2() {
        final var entity = new TestEntity();
        entity.setName("Test");
        testManager.insert(entity);
        entity.setName("Test Insert");
        assertThat(testManager.findById(entity.getId()).getName()).isEqualTo("Test Insert");
        testManager.detach(entity);
        assertThat(testManager.findById(entity.getId()).getName()).isNotEqualTo("Test Insert");
    }

    @Test
    public void mergeTest() {
        final var entity = new TestEntity();
        entity.setName("--------");
        testManager.startTransaction();
        testManager.insertNonTransactional(entity);
        entity.setName("mergeTest");
        testManager.merge(entity);
        testManager.completeTransaction();
        assertThat(testManager.findByName(entity.getName())).isNotNull();
    }

    @Test
    public void mergeTest2() {
        final var entity = new TestEntity();
        entity.setName("--------");
        testManager.startTransaction();
        testManager.insertNonTransactional(entity);
        testManager.flush();
        assertThat(testManager.findByName("--------")).isNotNull();
        testManager.detach(entity);
        entity.setName("mergeTest2");
        testManager.merge(entity);
        testManager.completeTransaction();
        assertThat(testManager.findByName("mergeTest2")).isNotNull();
    }

    @Test
    public void removeTest() {
        final var entity = new TestEntity();
        entity.setName("removeTest");
        testManager.insert(entity);
        final var e = testManager.findByName("removeTest");
        testManager.removeById(e.getId());
        assertThatThrownBy(() -> testManager.findByName("removeTest")).isInstanceOf(Exception.class);
    }

    @Test
    public void refreshTest() {
        final var entity = new TestEntity();
        entity.setName("refreshTest");
        final var e = testManager.insert(entity);
        e.setName("refreshTest2");
        testManager.refresh(e);
        assertThat(e.getName()).isEqualTo("refreshTest");
    }

}
