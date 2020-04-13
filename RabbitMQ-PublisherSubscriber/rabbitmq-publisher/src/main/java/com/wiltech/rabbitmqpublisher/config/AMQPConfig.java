package com.wiltech.rabbitmqpublisher.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wiltech.common.AMQPUtil;

@Configuration
public class AMQPConfig {

    // Create topic exchange exemplar Exchange
    @Bean
    Exchange createExemplarTopicExchange() {

        return ExchangeBuilder
                .topicExchange("MyTopicExchangeTemplate")
                .durable(true)
                .build();
    }

    // Create fanout exchange
    @Bean
    Exchange createFanoutTopicExchange() {

        return AMQPUtil.getFanoutExchange();
    }

    // config to serialize messages
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {

        return new Jackson2JsonMessageConverter();
    }

    // config to serialize messages
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());

        return rabbitTemplate;
    }
}
