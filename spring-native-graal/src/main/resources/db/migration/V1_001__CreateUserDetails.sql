drop table IF EXISTS user;
create TABLE user
(
    id       BIGINT NOT NULL auto_increment,
    username VARCHAR(255),
    password VARCHAR(255),
    active   BIT,
    PRIMARY KEY (id)
);

drop table IF EXISTS person;
create TABLE person
(
    id                   BIGINT       NOT NULL auto_increment,
    user_id              BIGINT       NOT NULL,
    first_name           VARCHAR(255) NOT NULL,
    last_name            VARCHAR(255) NOT NULL,
    email                VARCHAR(255) NOT NULL,
    gender               VARCHAR(80),
    phone_number         VARCHAR(80),
    date_of_birth        DATE,
    marital_status       VARCHAR(80),
    number_of_dependants INT,
    PRIMARY KEY (id)
);

drop view IF EXISTS user_details_view;
create VIEW user_details_view AS
SELECT u.id,
       p.id as person_id,
       p.first_name,
       p.last_name,
       u.username,
       u.password,
       p.date_of_birth,
       u.active
FROM user u,
     person p
WHERE u.id = p.user_id;
