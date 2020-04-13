package com.wiltech.rabbitmq.users.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.wiltech.common.AMQPUtil;

/**
 * Configuration class to create a Queue to bind to an Exchange and receive messages.
 */
@Configuration
@ComponentScan
public class RabbitMQConfig {

    private static final String FANOUT_EXCHANGE_QUEUE_NAME = "users-fanout-queue";

    // Deserializer to map json to Java classes
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //    // Deserializer to map json to Java classes using
    //    @Bean
    //    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
    //        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
    //        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
    //
    //        return rabbitTemplate;
    //    }

    // create queue to bind with fanout exchange
    @Bean
    Queue createUsersFanoutQueue() {

        return new Queue(FANOUT_EXCHANGE_QUEUE_NAME, true);
    }

    // Get an instance of fanout exchange
    @Bean
    FanoutExchange createFanoutExchange() {

        return AMQPUtil.getFanoutExchange();
    }

    // bind the fanout queue to fanout exchange exchange
    @Bean
    Binding bindUsersFanoutQueueToFanoutExchange() {

        return BindingBuilder
                .bind(createUsersFanoutQueue())
                .to(createFanoutExchange());
    }

    @Bean
    ConnectionFactory connectionFactory() {
        final CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setUsername("guest");
        cachingConnectionFactory.setPassword("guest");

        return cachingConnectionFactory;
    }

    @Bean
    MessageListenerContainer messageListenerContainer() {
        final SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory());
        simpleMessageListenerContainer.setQueues(createUsersFanoutQueue());
        simpleMessageListenerContainer.setMessageListener(new RabbitMQMessageListener());

        return simpleMessageListenerContainer;
    }
}
