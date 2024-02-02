CREATE TABLE migrations_test (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    val INTEGER NOT NULL
);

INSERT INTO migrations_test (name, val) VALUES ('test1', 1), ('test2', 2), ('test3', 3), ('test4', 4), ('test5', 5), ('test6', 6), ('test7', 7), ('test8', 8), ('test9', 9), ('test10', 10);
