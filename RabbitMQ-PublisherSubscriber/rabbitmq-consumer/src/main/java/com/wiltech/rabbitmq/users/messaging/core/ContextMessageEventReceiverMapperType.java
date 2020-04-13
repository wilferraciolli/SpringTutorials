package com.wiltech.rabbitmq.users.messaging.core;

import com.wiltech.core.DomainEvent;
import com.wiltech.core.MessageEvent;
import com.wiltech.rabbitmq.users.messaging.PersonCreatedEvent;
import com.wiltech.rabbitmq.users.messaging.UserCreatedEvent;

/**
 * Enum to specify only the messages interested by receiver.
 */
public enum ContextMessageEventReceiverMapperType implements MessageEvent {

    PERSON_CREATED("personCreatedEvent", PersonCreatedEvent.class),
    USER_CREATED("userCreatedEvent", UserCreatedEvent.class);

    private final String messageEventType;
    private final Class<? extends DomainEvent> eventClass;

    ContextMessageEventReceiverMapperType(final String messageEventType, final Class<? extends DomainEvent> eventClass) {
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
