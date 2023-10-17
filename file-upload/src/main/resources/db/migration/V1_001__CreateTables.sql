drop table IF EXISTS file;
create TABLE file
(
    id          VARCHAR(36) NOT NULL,
    name        VARCHAR(256) NOT NULL,
    type        VARCHAR(25) NOT NULL,
    data        BLOB,
    PRIMARY KEY (id)
);
