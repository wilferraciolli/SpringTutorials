package com.wiltech.rabbitmq.users.messages;

import com.wiltech.core.DomainEvent;
import com.wiltech.core.MessageEvent;

/**
 * Indicates which events are to be processed when message is received. This should contain events that are interested by tis micro service only.
 */
public enum UserMessageReceiverMapperType implements MessageEvent {

    USER_CREATED("userCreatedEvent", UserCreatedEvent.class);

    private final String messageEventType;
    private final Class<? extends DomainEvent> eventClass;

    UserMessageReceiverMapperType(final String messageEventType, final Class<? extends DomainEvent> eventClass) {
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
