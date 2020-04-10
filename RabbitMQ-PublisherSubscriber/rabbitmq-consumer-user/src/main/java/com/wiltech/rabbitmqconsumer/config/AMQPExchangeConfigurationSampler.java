package com.wiltech.rabbitmqconsumer.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class AMQPExchangeConfigurationSampler {

    // Creating exchanges with Spring bean
    @Bean
    Exchange exampleExchange() {

        return new TopicExchange("ExampleExchange");
    }

    // Create exchange with Queue builder
    @Bean
    Exchange builderDirectExchange() {

        return ExchangeBuilder
                .directExchange("BuilderExchange")
                .autoDelete()
                .internal()
                // .durable(true)
                // .withArgument()
                // .withArguments()
                .build();
    }

    // Create exchange with Queue builder
    @Bean
    Exchange builderTopicExchange() {

        return ExchangeBuilder
                .directExchange("TopicExchange")
                .autoDelete()
                .internal()
                .durable(true)
                .build();
    }

    // Create exchange with Queue builder
    @Bean
    Exchange builderFanoutExchange() {

        return ExchangeBuilder
                .fanoutExchange("FanoutExchange")
                .autoDelete()
                .internal()
                .durable(false)
                .build();
    }

    // Create exchange with Queue builder
    @Bean
    Exchange builderHeadersExchange() {

        return ExchangeBuilder
                .headersExchange("HeadersExchange")
                .internal()
                .durable(false)
                .ignoreDeclarationExceptions()
                .build();
    }
}
