CREATE TABLE migrations_test_2 (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

INSERT INTO migrations_test_2 (name) SELECT name FROM migrations_test WHERE val % 2 = 0;
