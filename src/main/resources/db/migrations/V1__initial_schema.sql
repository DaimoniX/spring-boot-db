CREATE TABLE migrations_test_1 (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    val INTEGER NOT NULL
);

INSERT INTO migrations_test_1 (name, val) VALUES ('test1', 1), ('test2', 2), ('test3', 3), ('test4', 4), ('test5', 5), ('test6', 6), ('test7', 7), ('test8', 8), ('test9', 9), ('test10', 10);

CREATE TABLE test_entity (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

ALTER TABLE test_entity
    ADD COLUMN `val` INT NULL AFTER `name`;
