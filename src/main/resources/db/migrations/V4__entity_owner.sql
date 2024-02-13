CREATE TABLE test_owner_entity (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    test_entity_id INTEGER NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO test_owner_entity (id, name, test_entity_id) VALUES (1, 'owner1', 1);
