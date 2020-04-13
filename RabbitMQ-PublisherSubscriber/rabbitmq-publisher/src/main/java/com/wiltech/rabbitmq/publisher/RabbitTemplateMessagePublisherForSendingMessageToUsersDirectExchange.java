package com.wiltech.rabbitmq.publisher;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
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
import com.wiltech.common.AMQPUtil;
import com.wiltech.rabbitmq.messages.UserCreatedEvent;
import com.wiltech.rabbitmq.messages.core.MessageSent;
import com.wiltech.rabbitmq.messages.core.MessageSentRepository;

/**
 * Exemplar to send message to a direct exchange.
 * It requires that the direct exchange exists and that the exchange and key are valid.
 */
@Service
public class RabbitTemplateMessagePublisherForSendingMessageToUsersDirectExchange {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageSentRepository messageSentRepository;

    @Value("${spring.application.name}")
    private String applicationName;

    @Scheduled(fixedRate = 1000L)
    public void publishMessage() throws JsonProcessingException {

        System.out.println("Publishing message to users direct exchange");

        sendUserCreatedMessage();
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

        sendMessageToDirectExchange(AMQPUtil.USERS_DIRECT_EXCHANGE, AMQPUtil.USERS_ROUTING_KEY, message);
    }

    private void sendMessageToDirectExchange(final String exchangeName, final String routingKey, final Message message) {
        persistDetailsForTheMessageSent(message);

        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }

    private void persistDetailsForTheMessageSent(Message message) {

        messageSentRepository.save(MessageSent.builder()
                .correlationId(message.getMessageProperties().getCorrelationId())
                .messageId(message.getMessageProperties().getMessageId())
                .appId(message.getMessageProperties().getAppId())
                .userId(message.getMessageProperties().getUserId())
                .eventType(message.getMessageProperties().getType())
                .replyTo(message.getMessageProperties().getReplyTo())
                .source(message.getMessageProperties().getHeader("source"))
                .eventBody(new String(message.getBody()))
                .sentDateTime(LocalDateTime.now())
                .build());
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

