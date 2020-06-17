package com.wiltech.rabbitmq.users.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.wiltech.common.AMQPExchangeKeyRoutingType;
import com.wiltech.common.AMQPUtil;

/**
 * Configuration class to manage AMQP connections.
 */
@Configuration
@ComponentScan
public class UserAMQPConfig {

    private static final String FANOUT_EXCHANGE_QUEUE_NAME = "users-fanout-queue";
    private static final String DIRECT_EXCHANGE_QUEUE_NAME = "users-queue";

    @Value("${spring.rabbitmq.username}")
    private String AMQP_USERNAME;

    @Value("${spring.rabbitmq.password}")
    private String AMQP_PASSWORD;

    // Deserializer to map json to Java classes
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // create queue to bind with fanout exchange
    @Bean
    Queue createUsersFanoutQueue() {

        return new Queue(FANOUT_EXCHANGE_QUEUE_NAME, true);
    }

    // create queue for direct messages (command)
    @Bean
    Queue createUsersDirectQueue() {

        return new Queue(DIRECT_EXCHANGE_QUEUE_NAME, true);
    }

    // Get an instance of fanout exchange
    @Bean
    FanoutExchange createFanoutExchange() {

        return AMQPUtil.getFanoutExchange();
    }

    // create this context direct exchange
    @Bean
    DirectExchange createUsersDirectExchange() {

        return ExchangeBuilder
                .directExchange(AMQPExchangeKeyRoutingType.USER_EXCHANGE.getExchangeName())
                .durable(true)
                .build();
    }

    // bind the fanout queue to fanout exchange
    @Bean
    Binding bindUsersFanoutQueueToFanoutExchange() {

        return BindingBuilder
                .bind(createUsersDirectQueue())
                .to(createUsersDirectExchange())
                .with(AMQPExchangeKeyRoutingType.USER_EXCHANGE.getKeyRouting());
    }

    // bind the users direct queue to users direct exchange
    @Bean
    Binding bindUsersDirectQueueToUsersDirectExchange() {

        return BindingBuilder
                .bind(createUsersFanoutQueue())
                .to(createFanoutExchange());
    }

    @Bean
    ConnectionFactory connectionFactory() {
        final CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setUsername(AMQP_USERNAME);
        cachingConnectionFactory.setPassword(AMQP_PASSWORD);

        return cachingConnectionFactory;
    }

    @Bean
    MessageListenerContainer messageListenerContainer() {
        final SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory());
        simpleMessageListenerContainer.setQueues(createUsersFanoutQueue(), createUsersDirectQueue());
        simpleMessageListenerContainer.setMessageListener(new AMQPFanoutMessageListener());

        return simpleMessageListenerContainer;
    }
}
