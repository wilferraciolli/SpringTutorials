package com.wiltech.rabbitmq.publisher;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiltech.messaging.CustomMessage;
import com.wiltech.rabbitmq.messages.PersonCreatedEvent;
import com.wiltech.rabbitmq.messages.UserCreatedEvent;

/**
 * This service shows how to post messages to a topic and add a routing key.
 */
@Service
public class RabbitTemplateMessagePublisherForTheExemplarTopicExchange {

    private static final String EXCHANGE_TOPIC_NAME = "MyTopicExchangeTemplate";
    private static final String ROUTING_KEY = "routing-key";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    @Scheduled(fixedRate = 60000L)
    public void publishMessage() throws JsonProcessingException {

        System.out.println("Publishing message from the exemplar");
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
                .setAppId(applicationName)
                .setMessageId(UUID.randomUUID().toString())
                .setType(resolveJsonRootName(event))
                .setContentEncoding("UTF-8")
                .setHeader("object", event.getClass().getCanonicalName())
                .setHeader("source", applicationName)
                .build();

        sendMessage(message);
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
                .setAppId(applicationName)
                .setMessageId(UUID.randomUUID().toString())
                .setType(resolveJsonRootName(event))
                .setContentEncoding("UTF-8")
                .setHeader("object", event.getClass().getCanonicalName())
                .setHeader("source", applicationName)
                .build();

        sendMessage(message);
    }

    private void sendCustomMessage() throws JsonProcessingException {

        final CustomMessage customMessageToSend = CustomMessage.builder()
                .id(2000L)
                .customDescription("Test")
                .price(4000L)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String s = objectMapper.writeValueAsString(customMessageToSend);

        final Message message = MessageBuilder.withBody(s.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(UUID.randomUUID().toString())
                .setAppId(applicationName)
                .setMessageId(UUID.randomUUID().toString())
                .setType(resolveJsonRootName(customMessageToSend))
                .setContentEncoding("UTF-8")
                .setHeader("object", customMessageToSend.getClass().getCanonicalName())
                .setHeader("source", applicationName)
                .build();

        sendMessage(message);
    }

    private void sendMessage(Message message) {
        persistDetailsForTheMessageSent(message);

        rabbitTemplate.convertAndSend(EXCHANGE_TOPIC_NAME, ROUTING_KEY, message);
    }

    private void persistDetailsForTheMessageSent(Message message) {

        // code implemented on the library
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

