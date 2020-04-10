drop table IF EXISTS messagereceived;
create TABLE messagereceived
(
    id                 BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    correlation_id     VARCHAR(36) NOT NULL,
    source             VARCHAR(80) NOT NULL,
    reply_to           VARCHAR(80),
    message_type       VARCHAR(80),
    message_body       VARCHAR(6000),
    received_date_time DATETIME
);
commit;
