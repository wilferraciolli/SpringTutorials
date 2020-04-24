package com.wiltech.receiver;

import static java.util.Objects.nonNull;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiltech.audit.MessageReceived;
import com.wiltech.audit.MessageReceivedRepository;
import com.wiltech.core.AppContextBeanUtil;
import com.wiltech.core.DomainEvent;
import com.wiltech.core.MessageEvent;

import lombok.extern.java.Log;

/**
 * Abstract class to process each message receive and to convert onto a Domain event and publish it.
 * This class will take an array of enums that tell which events to process.
 * Finally it will create a record on the database of the event published.
 */
@Log
public class AbstractMessageReceiver {

    protected <T extends Enum<T> & MessageEvent> void publishDomainEvents(final Message message, final Class<T>... msgEventClass) {

        try {
            if (nonNull(message.getMessageProperties())
                && isNotEmpty(message.getMessageProperties().getType())) {

                List<DomainEvent> messages = processMessage(message, msgEventClass);
                messages.stream()
                        .forEach(m -> {
                            log.fine("The event is " + m);
                            createRecordForMessageReceived(message);
                            AppContextBeanUtil.getBean(EventPublisher.class).publishEvent(m);
                        });

            } else {
                log.severe("Message properties.type cannot be null");
            }

        } catch (JsonProcessingException e) {
            log.severe("Could not find an event to deserialize the message " + e.getMessage());
        }
    }

    private <T extends Enum<T> & MessageEvent> List<DomainEvent> processMessage(Message message, Class<T>[] msgEventClass)
            throws JsonProcessingException {
        List<DomainEvent> messages;

        if (isNotBlank(message.getMessageProperties().getReceivedRoutingKey())) {
            // Determine if routing key was populated which means that it should throw an error for unknown types
            messages = processKeyRoutedMessage(message, msgEventClass);
        } else {
            // messages processed here were broad casted via fanout and may or may not be handled
            messages = processFanOutMessage(message, msgEventClass);
        }

        return messages;
    }

    /**
     * Process a message that should have been key routed to this receiver. It must expect it, otherwise will throw an error.
     */
    @SuppressWarnings("unchecked")
    private <T extends Enum<T> & MessageEvent> List<DomainEvent> processKeyRoutedMessage(final Message message, final Class<T>... msgEventClass)
            throws JsonProcessingException {

        // this should instantiate a bean
        final List<DomainEvent> domainEvents = convertKeyRoutedMessageToDomainEvent(message, msgEventClass);
        System.out.println("The list of domain events so far " + domainEvents);

        return domainEvents;
    }

    /**
     * Process message broad casted via FanOut, it may or may not be expecting it, so no errors should be thrown.
     */
    private <T extends Enum<T> & MessageEvent> List<DomainEvent> processFanOutMessage(Message message, Class<T>[] msgEventClass)
            throws JsonProcessingException {
        // this should instantiate a bean
        final List<DomainEvent> domainEvents = convertFanOutMessageToDomainEvent(message, msgEventClass);
        System.out.println("The list of domain events so far " + domainEvents);

        return domainEvents;
    }

    /**
     * Creates a domain event from a message that is intended to be handled by the receiver.
     * @param <T> The enum type for the message event.
     * @param message The message to create the domain event from.
     * @param msgEventType The enum type for the message event.
     * @return The domain event.
     */
    @SuppressWarnings("unchecked")
    private <T extends Enum<T> & MessageEvent> List<DomainEvent> convertKeyRoutedMessageToDomainEvent(final Message message,
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

    /**
     * Creates a domain event from a message that may or may not to be handled by the receiver.
     * @param <T> The enum type for the message event.
     * @param message The message to create the domain event from.
     * @param msgEventType The enum type for the message event.
     * @return The domain event.
     */
    private <T extends Enum<T> & MessageEvent> List<DomainEvent> convertFanOutMessageToDomainEvent(Message message, Class<T>[] msgEventType)
            throws JsonProcessingException {

        String messageType = message.getMessageProperties().getType();
        String messageBody = new String(message.getBody());

        // Determine if the message type is a known message type. If unknown, the message type will be logged and no further action
        // will be taken.
        List<Class<? extends DomainEvent>> eventClass = new ArrayList<>();
        List<DomainEvent> domainEvents = new ArrayList<>();
        try {
            MessageEvent.getMsgDomainEventByMessageType(messageType, msgEventType).forEach(t -> eventClass.add(t.getEventClass()));
        } catch (IllegalArgumentException e) {
            log.info("Unknown message type: " + messageType);
            return domainEvents;
        }

        ObjectMapper mapper = new ObjectMapper();
        for (Class<? extends DomainEvent> eClass : eventClass) {
            domainEvents.add(mapper.readValue(messageBody, eClass));
        }

        return domainEvents;
    }

    private void createRecordForMessageReceived(final Message message) {

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
