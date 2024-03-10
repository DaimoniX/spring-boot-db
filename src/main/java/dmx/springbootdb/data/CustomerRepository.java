package dmx.springbootdb.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class CustomerRepository {
    private final MongoTemplate mongoTemplate;

    public Customer save(Customer customer) {
        return mongoTemplate.save(customer);
    }

    public List<Customer> findAll() {
        return mongoTemplate.findAll(Customer.class);
    }

    public Customer findByFirstName(String firstName) {
        return mongoTemplate.query(Customer.class).matching(where("firstName").is(firstName)).firstValue();
    }

    public void findAndIncrementScoreByFirstName(String firstName) {
        mongoTemplate.update(Customer.class).matching(where("firstName").is(firstName)).apply(new Update().inc("score", 1)).findAndModify();
    }

    public List<Customer> findAllWithScoreGreaterThan(int score) {
        return mongoTemplate.query(Customer.class).matching(where("score").gt(score)).all();
    }
}
