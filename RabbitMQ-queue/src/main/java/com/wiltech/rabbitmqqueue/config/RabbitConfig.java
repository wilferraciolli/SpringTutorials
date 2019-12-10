package com.wiltech.rabbitmqqueue.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Rabbit config. Defines configuration for the RabiitMQ.
 * Here we are declaring a Queue with name orders-queue and an Exchange with name orders-exchange.
 * We also defined the binding between orders-queue and orders-exchange so that any message sent to orders-exchange with routing-key as orders-queue will be sent to orders-queue.
 */
@Configuration
public class RabbitConfig {

    public static final String QUEUE_ORDERS = "orders-queue";
       public static final String EXCHANGE_ORDERS = "orders-exchange";

    /**
     * Orders queue queue.
     * @return the queue
     */
    @Bean
    Queue ordersQueue() {
        return QueueBuilder.durable(QUEUE_ORDERS).build();
    }

//    /**
//     * Dead letter queue queue.
//     * @return the queue
//     */
//    @Bean
//    Queue deadLetterQueue() {
//        return QueueBuilder.durable(QUEUE_DEAD_ORDERS).build();
//    }

    /**
     * Orders exchange exchange.
     * @return the exchange
     */
    @Bean
    Exchange ordersExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_ORDERS).build();
    }

    /**
     * Binding binding.
     * @param ordersQueue the orders queue
     * @param ordersExchange the orders exchange
     * @return the binding
     */
    @Bean
    Binding binding(Queue ordersQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(ordersQueue).to(ordersExchange).with(QUEUE_ORDERS);
    }
}
