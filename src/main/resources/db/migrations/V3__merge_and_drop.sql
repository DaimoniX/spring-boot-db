CREATE TABLE migrations_test_3 (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    val INTEGER NOT NULL
);

INSERT INTO migrations_test_3 (name, val) SELECT migrations_test_2.name, val FROM migrations_test_2 JOIN migrations_test_1 ON migrations_test_2.name = migrations_test_1.name;

DROP TABLE migrations_test_1;
DROP TABLE migrations_test_2;
