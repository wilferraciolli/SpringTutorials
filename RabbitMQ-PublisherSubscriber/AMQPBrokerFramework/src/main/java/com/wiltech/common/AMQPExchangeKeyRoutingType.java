package com.wiltech.common;

/**
 * Enum to contain the exchange and key Routing details.
 */
public enum AMQPExchangeKeyRoutingType {

    /**
     * Fanout exchange amqp exchange key routing type. Represents broadcast.
     */
    FANOUT_EXCHANGE("fanout-exchange", null),

    /**
     * User exchange amqp exchange key routing type.
     */
    USER_EXCHANGE("users-direct-exchange", "users-command"),

    /**
     * Person exchange amqp exchange key routing type.
     */
    PERSON_EXCHANGE("person-direct-exchange", "person-command");

    private String exchangeName;
    private String keyRouting;

    AMQPExchangeKeyRoutingType(String exchangeName, String keyRouting) {
        this.exchangeName = exchangeName;
        this.keyRouting = keyRouting;
    }

    /**
     * Gets exchange name.
     * @return the exchange name
     */
    public String getExchangeName() {
        return exchangeName;
    }

    /**
     * Gets key routing.
     * @return the key routing
     */
    public String getKeyRouting() {
        return keyRouting;
    }
}
