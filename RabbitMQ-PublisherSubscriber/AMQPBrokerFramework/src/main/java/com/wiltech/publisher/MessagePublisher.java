package com.wiltech.publisher;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiltech.audit.MessageSent;
import com.wiltech.audit.MessageSentRepository;
import com.wiltech.common.AMQPExchangeKeyRoutingType;
import com.wiltech.common.DirectExchange;

/**
 * The type Message publisher.
 */
@Service
public class MessagePublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageSentRepository messageSentRepository;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Creates and send message. works out the exchange and key routing based on annotations on the message body passed in.
     * @param messageBody the message body
     * @throws JsonProcessingException the json processing exception
     */
    public void sendMessage(Object messageBody) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        final Message message = MessageBuilder.withBody(objectMapper.writeValueAsString(messageBody).getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(UUID.randomUUID().toString())
                .setAppId(applicationName)
                .setMessageId(UUID.randomUUID().toString())
                .setType(resolveJsonRootName(messageBody))
                .setContentEncoding("UTF-8")
                .setHeader("object", messageBody.getClass().getCanonicalName())
                .setHeader("source", applicationName)
                .build();

        sendMessage(message, messageBody);
    }

    private void sendMessage(final Message message, final Object messageBody) {
        persistDetailsForTheMessageSent(message);
        final AMQPExchangeKeyRoutingType exchangeType = getExchangeName(messageBody);

        rabbitTemplate.convertAndSend(exchangeType.getExchangeName(), exchangeType.getKeyRouting(), message);
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

    private AMQPExchangeKeyRoutingType getExchangeName(final Object messagePayload) {
        final Annotation[] annotations = messagePayload.getClass().getAnnotations();
        for (final Annotation annotation : annotations) {

            if (annotation instanceof DirectExchange) {

                return (((DirectExchange) annotation).value());
            }
        }

        // return the default fanout exchange if not specific exchange was specified
        return AMQPExchangeKeyRoutingType.FANOUT_EXCHANGE;
    }

    private String resolveJsonRootName(final Object messageToSend) {
        final Annotation[] annotations = messageToSend.getClass().getAnnotations();
        for (final Annotation annotation : annotations) {
            if (annotation instanceof JsonRootName) {

                return ((JsonRootName) annotation).value();
            }
        }

        return messageToSend.getClass().getSimpleName();
    }
}
