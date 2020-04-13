package com.wiltech.rabbitmq.users.messaging.events.handler;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.wiltech.rabbitmq.users.messaging.events.UserCreatedEvent;

import lombok.extern.java.Log;

@Component
@Log
public class UserEventHandler {

    // @TransactionalEventListener
    @EventListener
    public void handleUserEvent1(UserCreatedEvent event) {
        log.info("handleUserEvent ****************************************** Message handled " + event);
    }

}
