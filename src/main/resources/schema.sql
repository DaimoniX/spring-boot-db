CREATE TABLE IF NOT EXISTS users (
   user_id INT NOT NULL AUTO_INCREMENT,
   user_name VARCHAR(64) NOT NULL,
   user_email VARCHAR(64) NOT NULL,
   PRIMARY KEY (user_id),
   UNIQUE INDEX email_UNIQUE (user_email ASC)
);
