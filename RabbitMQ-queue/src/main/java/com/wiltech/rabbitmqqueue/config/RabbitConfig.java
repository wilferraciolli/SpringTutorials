package com.wiltech.rabbitmqqueue.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

/**
 * The type Rabbit config. Defines configuration for the RabiitMQ.
 * Here we are declaring a Queue with name orders-queue and an Exchange with name orders-exchange.
 * We also defined the binding between orders-queue and orders-exchange so that any message sent to orders-exchange with routing-key as orders-queue will be sent to orders-queue.
 */
@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

    public static final String QUEUE_ORDERS = "orders-queue";
    public static final String EXCHANGE_ORDERS = "orders-exchange";
    public static final String QUEUE_DEAD_ORDERS = "dead-orders-queue";

    /**
     * Orders queue queue.
     * @return the queue
     */
    @Bean
    Queue ordersQueue() {

        return QueueBuilder.durable(QUEUE_ORDERS)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DEAD_ORDERS)
                .withArgument("x-message-ttl", 15000) //if message is not consumed in 15 seconds send to DLQ
                .build();
    }

    /**
     * Dead letter queue queue.
     * @return the queue
     */
    @Bean
    Queue deadLetterQueue() {

        return QueueBuilder.durable(QUEUE_DEAD_ORDERS).build();
    }

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

    /**
     * Method to force RabbitTemplate to be converted to JSON on every message.
     * We can configure org.springframework.amqp.support.converter.Jackson2JsonMessageConverter bean to be used by RabbitTemplate so that the message will be serialized as JSON instead of byte[].
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());

        return rabbitTemplate;
    }

    /**
     * We can configure org.springframework.amqp.support.converter.Jackson2JsonMessageConverter bean to be used by RabbitTemplate so that the message will be serialized as JSON instead of byte[].
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {

        return new Jackson2JsonMessageConverter();
    }

    //In order to treat the message payload as JSON we should customize the RabbitMQ configuration by implementing RabbitListenerConfigurer.
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    //In order to treat the message payload as JSON we should customize the RabbitMQ configuration by implementing RabbitListenerConfigurer.
    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());

        return messageHandlerMethodFactory;
    }

    //In order to treat the message payload as JSON we should customize the RabbitMQ configuration by implementing RabbitListenerConfigurer.
    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {

        return new MappingJackson2MessageConverter();
    }
}
