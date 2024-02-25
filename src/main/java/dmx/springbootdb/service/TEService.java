package dmx.springbootdb.service;

import dmx.springbootdb.dao.TestEntity;
import dmx.springbootdb.dao.TestEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TEService {
    private final TestEntityRepository testEntityRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void addEntityTransactional(String name) {
        testEntityRepository.save(new TestEntity(name, 55));
    }
}
