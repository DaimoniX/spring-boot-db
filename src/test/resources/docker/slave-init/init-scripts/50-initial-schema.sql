CREATE DATABASE tests;

CREATE TABLE tests.product (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

ALTER TABLE tests.product
    ADD COLUMN `val` INT NULL AFTER `name`;
