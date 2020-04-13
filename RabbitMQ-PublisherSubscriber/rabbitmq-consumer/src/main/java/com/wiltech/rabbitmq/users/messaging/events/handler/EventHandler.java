package com.wiltech.rabbitmq.users.messaging.events.handler;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.wiltech.messaging.CustomMessage;
import com.wiltech.rabbitmq.users.messaging.PersonCreatedEvent;
import com.wiltech.rabbitmq.users.messaging.UserCreatedEvent;

import lombok.extern.java.Log;

/**
 * Class to demonstrate how to handle events. PS note that the @TransactionalEventListener event handler will only work if a transaction exists.
 */
@Component
@Log
public class EventHandler {

    @EventListener
    public void handleCustomMessageEvent(CustomMessage event) {
        log.info("handleCustomMessageEvent ****************************************** Message handled " + event);

    }

    //    @TransactionalEventListener
    @EventListener
    public void handlePersonEvent1(PersonCreatedEvent event) {
        log.info("handlePersonEvent ****************************************** Message handled " + event);
    }

    //    @TransactionalEventListener
    //    public void handlePersonEvent2(PersonCreatedEvent event) {
    //        log.info("handlePersonEvent2 ****************************************** Message handled " + event);
    //    }

    @EventListener
    public void handleUserEvent(UserCreatedEvent event) {
        log.info("handleUserEvent ****************************************** Message handled " + event);
    }
}
