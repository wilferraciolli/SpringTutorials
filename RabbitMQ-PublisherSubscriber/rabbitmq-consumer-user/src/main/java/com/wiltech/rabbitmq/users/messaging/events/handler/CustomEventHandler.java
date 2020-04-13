package com.wiltech.rabbitmq.users.messaging.events.handler;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.wiltech.messaging.CustomMessage;

import lombok.extern.java.Log;

@Component
@Log
public class CustomEventHandler {

    @EventListener
    public void handleCustomMessageEvent(CustomMessage event) {
        log.severe(
                "THIS SHOULD NEVER HAPPEN AS WE NOT SUBSCRIBED TO IT ****************************************** Message handled "
                + event);

    }

}
