package com.wiltech.common;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;

public final class AMQPUtil {

    // Broadcaster
    public static final String FANOUT_EXCHANGE = "fanout-exchange";

    // individual user exchange
    public static final String USERS_DIRECT_EXCHANGE = "";
    public static final String USERS_ROUTING_KEY = "";
    public static final String USERS_QUEUE_NAME = "";

    // individual people exchange
    public static final String PERSON_DIRECT_EXCHANGE = "";
    public static final String PERSON_ROUTING_KEY = "";
    public static final String PERSON_QUEUE_NAME = "";

    // Create fanout exchange
    public static final FanoutExchange getFanoutExchange() {

        return ExchangeBuilder
                .fanoutExchange(FANOUT_EXCHANGE)
                .durable(true)
                .build();
    }

}
