package com.wiltech.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.java.Log;

@Log
public class AbstractMessageReceiver {

    protected <T extends Enum<T> & MessageEvent> void publishDomainEvents(Message message, final Class<T>... msgEventClass) {

        try {
            List<DomainEvent> messages = processQueueMessage(message, msgEventClass);
            messages.stream()
                    .forEach(event -> {
                        log.fine("The event is " + event);
                        createRecordForMessageReceived(message);
                        AppContextBeanUtil.getBean(EventPublisher.class).publishEvent(event);
                    });

        } catch (JsonProcessingException e) {
            log.severe("Could not find an event to deserialize the message " + e.getMessage());
        }
    }

    /**
     * Process the message from a queue.
     * @param <T> The enum type for the message event.
     * @param message The message to be processed.
     * @param msgEventClass The enum types for the message event.
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T extends Enum<T> & MessageEvent> List<DomainEvent> processQueueMessage(final Message message, final Class<T>... msgEventClass)
            throws JsonProcessingException {

        // this should instantiate a bean
        final List<DomainEvent> domainEvents = convertQueueMessageToDomainEvent(message, msgEventClass);
        System.out.println("The list of domain events so far " + domainEvents);

        return domainEvents;

    }

    /**
     * Creates a domain event from a message from a queue.
     * @param <T> The enum type for the message event.
     * @param message The message to create the domain event from.
     * @param msgEventType The enum type for the message event.
     * @return The domain event.
     */
    @SuppressWarnings("unchecked")
    private <T extends Enum<T> & MessageEvent> List<DomainEvent> convertQueueMessageToDomainEvent(final Message message,
            final Class<T>... msgEventType) throws JsonProcessingException {

        String messageType = message.getMessageProperties().getType();
        String messageBody = new String(message.getBody());

        List<Class<? extends DomainEvent>> eventClass = new ArrayList<>();
        MessageEvent.getMsgDomainEventByMessageType(messageType, msgEventType).forEach(t -> eventClass.add(t.getEventClass()));

        ObjectMapper mapper = new ObjectMapper();
        List<DomainEvent> domainEvents = new ArrayList<>();
        for (Class<? extends DomainEvent> eClass : eventClass) {
            domainEvents.add(mapper.readValue(messageBody, eClass));
        }

        return domainEvents;
    }

    private void createRecordForMessageReceived(Message message) {
        AppContextBeanUtil.getBean(MessageReceivedRepository.class).save(MessageReceived.builder()
                .correlationId(message.getMessageProperties().getCorrelationId())
                .messageId(message.getMessageProperties().getMessageId())
                .appId(message.getMessageProperties().getAppId())
                .userId(message.getMessageProperties().getUserId())
                .receivedUserId(message.getMessageProperties().getReceivedUserId())
                .eventType(message.getMessageProperties().getType())
                .contentType(message.getMessageProperties().getContentType())
                .encodingType(message.getMessageProperties().getContentEncoding())
                .replyTo(message.getMessageProperties().getReplyTo())
                .source(message.getMessageProperties().getHeader("source"))
                .receivedExchange(message.getMessageProperties().getReceivedExchange())
                .receivedRoutingKey(message.getMessageProperties().getReceivedRoutingKey())
                .consumerTag(message.getMessageProperties().getConsumerTag())
                .consumerQueue(message.getMessageProperties().getConsumerQueue())
                .eventBody(new String(message.getBody()))
                .receivedDateTime(LocalDateTime.now())
                .build());
    }
}
