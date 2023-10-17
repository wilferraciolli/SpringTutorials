drop table IF EXISTS file;
create TABLE file
(
    id          VARCHAR(36) NOT NULL,
    name        VARCHAR(512) NOT NULL,
    type        VARCHAR(252) NOT NULL,
    data        BLOB,
    PRIMARY KEY (id)
);
