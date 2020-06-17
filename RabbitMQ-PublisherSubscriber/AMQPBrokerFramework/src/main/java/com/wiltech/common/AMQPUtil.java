package com.wiltech.common;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;

public final class AMQPUtil {

    // Create fanout exchange
    public static final FanoutExchange getFanoutExchange() {

        return ExchangeBuilder
                .fanoutExchange(AMQPExchangeKeyRoutingType.FANOUT_EXCHANGE.getExchangeName())
                .durable(true)
                .build();
    }

}
