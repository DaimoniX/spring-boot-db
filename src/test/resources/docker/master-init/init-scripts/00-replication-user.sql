CREATE USER 'replication_user'@'%' IDENTIFIED BY 'replica';
GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'%';
