package com.wiltech.rabbitmq.users.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;

//@Configuration
public class AMQPQueueConfigurationSampler {

    // Creating queues with Spring bean
    @Bean
    Queue exampleQueue() {

        return new Queue("ExampleQueue", false);
    }

    // Create queue with Queue builder
    @Bean
    Queue builderQueue() {

        return QueueBuilder
                .durable("QueryBuilderQueue")
                .autoDelete()
                .exclusive()
                // .withArgument()
                // .withArguments()
                .build();
    }
}
