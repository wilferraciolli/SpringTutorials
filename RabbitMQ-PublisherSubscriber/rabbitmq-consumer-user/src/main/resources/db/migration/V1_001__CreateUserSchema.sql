-- create user schema
drop table IF EXISTS messagereceived;
create TABLE messagereceived
(
    id                   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    correlation_id       VARCHAR(36)  NOT NULL,
    message_id           VARCHAR(36)  NOT NULL,
    app_id               VARCHAR(500) NULL,
    user_id              VARCHAR(36)  NULL,
    received_user_id     VARCHAR(36)  NULL,
    source               VARCHAR(80)  NULL,
    reply_to             VARCHAR(80),
    event_type           VARCHAR(80),
    content_type         VARCHAR(80),
    encoding_type        VARCHAR(80),
    event_body           VARCHAR(6000),
    received_exchange    VARCHAR(200),
    received_routing_key VARCHAR(200),
    consumer_tag         VARCHAR(200),
    consumer_queue       VARCHAR(200),
    received_date_time   DATETIME
);
commit;

