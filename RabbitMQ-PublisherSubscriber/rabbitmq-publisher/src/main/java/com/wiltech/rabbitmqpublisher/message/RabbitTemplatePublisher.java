package com.wiltech.rabbitmqpublisher.message;

import java.lang.annotation.Annotation;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonRootName;

@Service
public class RabbitTemplatePublisher {

    private static final String EXCHANGE_NAME = "MyTopicExchange";
    private static final String ROUTING_KEY = "routing-key";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 5000L)
    public void publishMessage() {

        //        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "My message"); //send string as message
        //        rabbitTemplate.convertAndSend("My message"); //send message tot he default exchange

        final MessageToSend messageToSend = MessageToSend.builder()
                .Id(1000L)
                .description("Test")
                .amount(2000L)
                .build();

        resolveJsonRootName(messageToSend);

        System.out.println("Publishing message");
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, messageToSend);
    }

    private void resolveJsonRootName(final MessageToSend messageToSend) {
        final Annotation[] annotations = messageToSend.getClass().getAnnotations();
        for (final Annotation annotation : annotations) {
            if (annotation instanceof JsonRootName) {
                System.out.println("The json root name is :" + ((JsonRootName) annotation).value());
            }

        }
    }
}
