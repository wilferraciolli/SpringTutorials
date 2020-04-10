package com.wiltech.rabbitmqpublisher.messages;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RabbitTemplatePublisher {

    private static final String EXCHANGE_NAME = "MyTopicExchange";
    private static final String ROUTING_KEY = "routing-key";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 5000L)
    public void publishMessage() throws JsonProcessingException {

        //        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "My message"); //send string as message
        //        rabbitTemplate.convertAndSend("My message"); //send message tot he default exchange

        final MessageToSend messageToSend = MessageToSend.builder()
                .id(1000L)
                .description("Test")
                .amount(2000L)
                .build();

        resolveJsonRootName(messageToSend);

        System.out.println("Publishing message");
        // rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, messageToSend);

        sendCustomMessage();
        sendUserCreatedMessage();
        sendPersonCreatedMessage();
    }

    private void sendPersonCreatedMessage() throws JsonProcessingException {

        final PersonCreatedEvent event = PersonCreatedEvent.builder()
                .personId(5000L)
                .firstName("wil")
                .lastName("wil")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String s = objectMapper.writeValueAsString(event);

        final Message message = MessageBuilder.withBody(s.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(UUID.randomUUID().toString())
                .setType(resolveJsonRootName(event))
                .setContentEncoding("UTF-8")
                .setHeader("object", event.getClass().getCanonicalName())
                .build();

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
    }

    private void sendUserCreatedMessage() throws JsonProcessingException {

        final UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(2000L)
                .email("email@email.com")
                .roles(Arrays.asList("ADMIN", "USER"))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String s = objectMapper.writeValueAsString(event);

        final Message message = MessageBuilder.withBody(s.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(UUID.randomUUID().toString())
                .setType(resolveJsonRootName(event))
                .setContentEncoding("UTF-8")
                .setHeader("object", event.getClass().getCanonicalName())
                .build();

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
    }

    private void sendCustomMessage() throws JsonProcessingException {

        final CustomMessageToSend customMessageToSend = CustomMessageToSend.builder()
                .id(2000L)
                .customDescription("Test")
                .price(4000L)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String s = objectMapper.writeValueAsString(customMessageToSend);

        final Message message = MessageBuilder.withBody(s.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(UUID.randomUUID().toString())
                .setType(resolveJsonRootName(customMessageToSend))
                .setContentEncoding("UTF-8")
                .setHeader("object", customMessageToSend.getClass().getCanonicalName())
                .build();

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
    }

    private String resolveJsonRootName(final Object messageToSend) {
        final Annotation[] annotations = messageToSend.getClass().getAnnotations();
        for (final Annotation annotation : annotations) {
            if (annotation instanceof JsonRootName) {
                System.out.println("The json root name is :" + ((JsonRootName) annotation).value());

                return ((JsonRootName) annotation).value();
            }
        }

        return messageToSend.getClass().getSimpleName();
    }
}

