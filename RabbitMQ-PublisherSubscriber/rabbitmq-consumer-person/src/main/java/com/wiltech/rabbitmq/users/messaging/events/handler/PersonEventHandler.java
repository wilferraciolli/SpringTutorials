package com.wiltech.rabbitmq.users.messaging.events.handler;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.wiltech.rabbitmq.users.messaging.events.PersonCreatedEvent;

import lombok.extern.java.Log;

@Component
@Log
public class PersonEventHandler {

    // @TransactionalEventListener
    @EventListener
    public void handlePersonEvent1(PersonCreatedEvent event) {
        log.info("handlePersonEvent ****************************************** Message handled " + event);
    }

}
