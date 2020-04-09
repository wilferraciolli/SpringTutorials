package com.wiltech.rabbitmqconsumer.messages.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractMessageReceiver {

    /**
     * Process the message from a queue.
     * @param <T> The enum type for the message event.
     * @param message The message to be processed.
     * @param msgEventClass The enum types for the message event.
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T extends Enum<T> & MessageEvent> List<DomainEvent> processQueueMessage(final Message message, final Class<T>... msgEventClass)
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
}
