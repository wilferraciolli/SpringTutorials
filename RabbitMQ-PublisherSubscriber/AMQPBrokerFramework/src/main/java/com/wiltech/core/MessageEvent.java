package com.wiltech.core;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public interface MessageEvent {

    String getMessageType();

    Class<? extends DomainEvent> getEventClass();

    /**
     * Look up the message event for the given message type.
     * @param <T> The enum type.
     * @param messageType The message type to obtain the domain event for.
     * @param enumClass The enum type.
     * @return The message event.
     */
    @SafeVarargs
    static <T extends Enum<T> & MessageEvent> List<T> getMsgDomainEventByMessageType(final String messageType, final Class<T>... enumClass) {
        List<T> messageEvents = new ArrayList<>();
        for (Class<T> eClass : Arrays.asList(enumClass)) {
            if (MessageEvent.class.isAssignableFrom(eClass)) {
                messageEvents.addAll(EnumSet.allOf(eClass).stream().filter(e -> e.getMessageType().equals(messageType)).collect(toList()));
            }
        }
        if (messageEvents.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Domain event(s) for the message type %s cannot be found.", messageType));
        }

        return messageEvents;
    }
}
