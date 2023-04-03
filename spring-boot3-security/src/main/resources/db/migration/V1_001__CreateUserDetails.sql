drop table IF EXISTS users;
create TABLE users
(
    id          BIGINT NOT NULL auto_increment,
    role_type   VARCHAR(25) NOT NULL,
    first_name  VARCHAR(25) NOT NULL,
    last_name   VARCHAR(25) NOT NULL,
    username    VARCHAR(255),
    password    VARCHAR(255),
    active      BIT,
    PRIMARY KEY (id)
);

drop table IF EXISTS token;
create TABLE token
(
    id                   BIGINT       NOT NULL auto_increment,
    user_id              BIGINT       NOT NULL,
    token                VARCHAR(2000)  NULL,
    token_type           VARCHAR(25)  NULL,
    revoked              BIT,
    expired              BIT,
    PRIMARY KEY (id)
);

