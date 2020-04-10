create database if not exists publisher;
commit;

drop table IF EXISTS messagesent;
create TABLE messagesent
(
    id             BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    correlation_id VARCHAR(36) NOT NULL,
    message_id     VARCHAR(36) NOT NULL,
    app_id         VARCHAR(36) NULL,
    user_id        VARCHAR(36) NULL,
    source         VARCHAR(80) NOT NULL,
    reply_to       VARCHAR(80),
    event_type     VARCHAR(80),
    event_body     VARCHAR(6000),
    sent_date_time DATETIME
);
commit;
