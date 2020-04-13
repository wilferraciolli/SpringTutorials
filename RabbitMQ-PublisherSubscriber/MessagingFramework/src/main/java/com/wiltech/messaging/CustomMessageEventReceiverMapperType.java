package com.wiltech.messaging;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.wiltech.core.DomainEvent;
import com.wiltech.core.MessageEvent;

/**
 * Message Event Receiver Mapper Type to demonstrate how to pass an array of message-event mappers.
 */
public enum CustomMessageEventReceiverMapperType implements MessageEvent {

    USER_CREATED("myCustomMessage", CustomMessage.class);

    private final String messageEventType;
    private final Class<? extends DomainEvent> eventClass;

    CustomMessageEventReceiverMapperType(final String messageEventType, final Class<? extends DomainEvent> eventClass) {
        this.messageEventType = messageEventType;
        this.eventClass = eventClass;
    }

    /**
     * Get a list of events declared within the message-event-mapper. This can be used to know the list of interested events.
     * @return The list of interested events.
     */
    protected List<String> getEventsType() {

        return Arrays.stream(CustomMessageEventReceiverMapperType.values())
                .map(event -> event.messageEventType)
                .collect(Collectors.toList());
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
