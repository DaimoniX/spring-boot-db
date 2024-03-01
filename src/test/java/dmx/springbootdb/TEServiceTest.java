package dmx.springbootdb;

import dmx.springbootdb.service.TEService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TEServiceTest {
    @Container
    @ServiceConnection
    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.3"));
    @Autowired
    private TEService teService;

    @BeforeAll
    public static void beforeAll() {
        MY_SQL_CONTAINER.start();
    }

    @AfterAll
    public static void afterAll() {
        MY_SQL_CONTAINER.stop();
    }

    @DynamicPropertySource
    public static void configureTestProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    private static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map.Entry<Integer, Integer> pair(Integer a, Integer b) {
        return Map.entry(a, b);
    }

    @Test
    public void containerStatusTest() {
        assertThat(MY_SQL_CONTAINER.isCreated()).isTrue();
        assertThat(MY_SQL_CONTAINER.isRunning()).isTrue();
    }

    @Test
    @Transactional
    public void testNeverFail() {
        assertThatThrownBy(() -> teService.addEntityTransactionalNever("fail")).isInstanceOf(IllegalTransactionStateException.class);
    }

    @Test
    public void testNever() {
        teService.addEntityTransactionalNever("testNever1");
        assertThat(teService.getWithName("testNever1")).isNotNull();
    }

    @Test
    @Transactional
    public void testNested() {
        assertThatThrownBy(() -> teService.addEntityTransactionalNested("fail")).isInstanceOf(NestedTransactionNotSupportedException.class);
    }

    @Test
    public void testSupportsNoTransaction() {
        teService.addEntityTransactionalSupports("testSupportsNoTransaction1", false);
        assertThat(teService.getWithName("testSupportsNoTransaction1")).isNotNull();

        assertThatThrownBy(() -> teService.addEntityTransactionalSupports("testSupportsNoTransaction2", true)).isInstanceOf(RuntimeException.class);

        assertThat(teService.getWithName("testSupportsNoTransaction2")).isNotNull();
    }

    @Test
    public void testSupportsTransaction() {
        teService.addEntityTransactionalSupportsWrapper("testSupportsTransaction1", false);
        assertThat(teService.getWithName("testSupportsTransaction1")).isNotNull();

        assertThatThrownBy(() -> teService.addEntityTransactionalSupportsWrapper("testSupportsTransaction2", true)).isInstanceOf(RuntimeException.class);

        assertThat(teService.getWithName("testSupportsTransaction2")).isNull();
    }

    @Test
    public void testMandatoryFail() {
        assertThatThrownBy(() -> teService.addEntityTransactionalMandatory("testMandatory")).isInstanceOf(IllegalTransactionStateException.class);
    }

    @Test
    @Transactional
    public void testMandatory() {
        teService.addEntityTransactionalMandatory("testMandatory");
        assertThat(teService.getWithName("testMandatory")).isNotNull();
    }

    @Test
    public void testNonSupported() {
        teService.addEntityTransactionalNotSupported("testNonSupportedOk", false);
        assertThat(teService.getWithName("testNonSupportedOk")).isNotNull();

        try {
            teService.addEntityTransactionalNotSupported("testNonSupportedFail", true);
        } catch (Exception e) {

        }

        assertThat(teService.getWithName("testNonSupportedFail")).isNotNull();
    }

    @Test
    @Transactional
    public void testRequiresNew() {
        teService.addEntityTransactionalRequired("testRequiredOk", false);
        teService.addEntityTransactionalRequiresNew("testRequiredNewOk", false);
        assertThat(teService.getWithName("testRequiredNewOk")).isNotNull();

        try {
            teService.addEntityTransactionalRequiresNew("testRequiredNewFail", true);
        } catch (Exception e) {

        }

        assertThat(teService.getWithName("testRequiredOk")).isNotNull();
        assertThat(teService.getWithName("testRequiredNewFail")).isNull();

        assertThatThrownBy(() -> teService.addEntityTransactionalRequiredAndRequiresNew("testRequiredAndRequiresNew")).isInstanceOf(RuntimeException.class);

        assertThat(teService.getWithName("testRequiredAndRequiresNew-req")).isNotNull();
        assertThat(teService.getWithName("testRequiredAndRequiresNew-req_new")).isNull();
    }

    @Nested
    class IsolationTests {
        @BeforeEach
        public void setUp() {
            if (teService.getWithName("isolation1") == null)
                teService.addEntityTransactionalSupports("isolation1", false);
            if (teService.getWithName("isolation2") == null)
                teService.addEntityTransactionalSupports("isolation2", false);
        }

        @Test
        public void defaultTest() {
            teService.setEntityValue("isolation1", 0);
            assertThat(teService.getWithName("isolation1").getVal()).isEqualTo(0);
            teService.setEntityValue("isolation2", 0);
            assertThat(teService.getWithName("isolation2").getVal()).isEqualTo(0);

            final var result = new AtomicReference<Map.Entry<Integer, Integer>>(null);

            final var setThread = new Thread(() -> teService.setEntityValuesDefault("isolation", 500));

            setThread.start();

            sleep(100);
            result.set(teService.getEntityValuesDefault("isolation"));

            try {
                setThread.join();
            } catch (InterruptedException e) {
            }

            assertThat(result.get()).isEqualTo(pair(0, 0));
        }

        @Test
        public void readUncommittedTest() {
            teService.setEntityValue("isolation1", 0);
            assertThat(teService.getWithName("isolation1").getVal()).isEqualTo(0);
            teService.setEntityValue("isolation2", 0);
            assertThat(teService.getWithName("isolation2").getVal()).isEqualTo(0);

            final var result = new AtomicReference<Map.Entry<Integer, Integer>>(null);

            final var setThread = new Thread(() -> teService.setEntityValuesReadUncommitted("isolation", 500));

            setThread.start();

            sleep(100);
            result.set(teService.getEntityValuesReadUncommitted("isolation"));

            try {
                setThread.join();
            } catch (InterruptedException e) {
            }

            assertThat(result.get()).isEqualTo(pair(500, 0));
        }

        @Test
        public void committedTest() {
            teService.setEntityValue("isolation1", 0);
            assertThat(teService.getWithName("isolation1").getVal()).isEqualTo(0);
            teService.setEntityValue("isolation2", 0);
            assertThat(teService.getWithName("isolation2").getVal()).isEqualTo(0);

            final var result = new AtomicReference<Map.Entry<Integer, Integer>>(null);

            final var setThread = new Thread(() -> teService.setEntityValuesCommitted("isolation", 500));

            setThread.start();

            sleep(100);
            result.set(teService.getEntityValuesCommitted("isolation"));

            try {
                setThread.join();
            } catch (InterruptedException e) {
            }

            assertThat(result.get()).isEqualTo(pair(0, 0));
        }

        @Test
        public void repeatableReadTest() {
            teService.setEntityValue("isolation1", 0);
            assertThat(teService.getWithName("isolation1").getVal()).isEqualTo(0);

            final var result = new AtomicReference<Map.Entry<Integer, Integer>>(null);

            final var setThread = new Thread(() -> {
                teService.setEntityValuesRepeatableRead("isolation", 500);
                sleep(500);
                teService.setEntityValuesRepeatableRead("isolation", 1000);
            });

            setThread.start();

            sleep(100);
            result.set(teService.getEntityValuesRepeatableRead("isolation", 1000));

            try {
                setThread.join();
            } catch (InterruptedException e) {
            }

            assertThat(result.get()).isEqualTo(pair(500, 500));
        }

        @Test
        public void serializableTest() {
            teService.setEntityValue("isolation1", 0);
            assertThat(teService.getWithName("isolation1").getVal()).isEqualTo(0);
            teService.setEntityValue("isolation2", 0);
            assertThat(teService.getWithName("isolation2").getVal()).isEqualTo(0);

            final var index = new AtomicInteger();
            final var result = new AtomicIntegerArray(4);

            final var setThread = new Thread(() -> teService.setEntityValuesSerializable("isolation", 1000, (val) -> result.set(index.getAndIncrement(), val), (val) -> result.set(index.getAndIncrement(), val)));

            setThread.start();

            sleep(50);
            teService.setEntityValuesSerializable("isolation", 2000, (val) -> result.set(index.getAndIncrement(), val), (val) -> result.set(index.getAndIncrement(), val));

            try {
                setThread.join();
            } catch (InterruptedException e) {
            }

            assertThat(result.get(0)).isEqualTo(1000);
            assertThat(result.get(1)).isEqualTo(1500);
            assertThat(result.get(2)).isEqualTo(2000);
            assertThat(result.get(3)).isEqualTo(2500);
        }
    }
}
