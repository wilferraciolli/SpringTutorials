package com.wiltech.rabbitmq.users.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "MyTemplateQueue";

    @Value("${spring.rabbitmq.username}")
    private String AMQP_USERNAME;

    @Value("${spring.rabbitmq.password}")
    private String AMQP_PASSWORD;

    // Deserializer to map json to Java classes
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Deserializer to map json to Java classes using Rabbit Template
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());

        return rabbitTemplate;
    }

    // Create topic Exchange if not exists, this is an exemplar
    @Bean
    Exchange myExemplarTopicExchange() {

        return ExchangeBuilder
                .topicExchange("MyTopicExchangeTemplate")
                .durable(true)
                .build();
    }

    // create queue
    @Bean
    Queue myExemplarQueue() {

        return new Queue(QUEUE_NAME, true);
    }

    // bind the exemplar queue to the exemplar topic exchange
    @Bean
    Binding myTopicExchangeExemplarQueueBinding() {

        return BindingBuilder
                .bind(myExemplarQueue())
                .to(myExemplarTopicExchange())
                .with("routing-key")
                .noargs();
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
        simpleMessageListenerContainer.setQueues(myExemplarQueue());
        simpleMessageListenerContainer.setMessageListener(new RabbitMQMessageListener());

        return simpleMessageListenerContainer;
    }
}
