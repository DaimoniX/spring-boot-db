CREATE TABLE test_entity (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

ALTER TABLE test_entity
    ADD COLUMN `val` INT NULL AFTER `name`;
