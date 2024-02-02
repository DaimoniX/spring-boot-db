package dmx.springbootdb;

import dmx.springbootdb.emanager.TestEntity;
import dmx.springbootdb.emanager.TransactionTestManager;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EntityManagerTest {
    @Autowired
    private TransactionTestManager testManager;

    @Test
    public void insertTest() {
        final var entity = new TestEntity();
        entity.setId(1);
        entity.setName("Test");
        testManager.insert(entity);
        assertThat(testManager.findById(1)).isNotNull();
    }

    @Test
    public void findTest() {
        final var entity = new TestEntity();
        entity.setId(1);
        entity.setName("Test");
        testManager.insert(entity);
        assertThat(testManager.findByName("Test")).isNotNull();
    }
}
