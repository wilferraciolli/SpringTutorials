package com.wiltech.rabbitmqconsumer.messages.core;

import com.wiltech.core.DomainEvent;
import com.wiltech.core.MessageEvent;
import com.wiltech.rabbitmqconsumer.messages.CustomMessage;
import com.wiltech.rabbitmqconsumer.messages.PersonCreatedEvent;
import com.wiltech.rabbitmqconsumer.messages.UserCreatedEvent;

/**
 * Enum to specify only the messages interested by receiver.
 */
public enum MessageReceiverType implements MessageEvent {

    TEST("myCustomMessage", CustomMessage.class),
    PERSON_CREATED("personCreatedEvent", PersonCreatedEvent.class),
    USER_CREATED("userCreatedEvent", UserCreatedEvent.class);

    private final String messageEventType;
    private final Class<? extends DomainEvent> eventClass;

    MessageReceiverType(final String messageEventType, final Class<? extends DomainEvent> eventClass) {
        this.messageEventType = messageEventType;
        this.eventClass = eventClass;
    }

    @Override
    public String getMessageType() {
        return messageEventType;
    }

    @Override
    public Class<? extends DomainEvent> getEventClass() {
        return eventClass;
    }
}
