package com.wiltech.rabbitmqconsumer.messages.events.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.wiltech.rabbitmqconsumer.messages.CustomMessage;
import com.wiltech.rabbitmqconsumer.messages.PersonCreatedEvent;
import com.wiltech.rabbitmqconsumer.messages.UserCreatedEvent;

import lombok.extern.java.Log;

@Component
@Log
public class CustomMessageHandler {

    //  @EventListener
    public void handleEvent(CustomMessage event) {
        log.info("****************************************** Message handled " + event);

    }

    @TransactionalEventListener
    public void handlePersonEvent1(PersonCreatedEvent event) {
        log.info("handlePersonEvent1 ****************************************** Message handled " + event);
    }

    @TransactionalEventListener
    public void handlePersonEvent2(PersonCreatedEvent event) {
        log.info("handlePersonEvent2 ****************************************** Message handled " + event);
    }

    @TransactionalEventListener
    public void handleUserEvent(UserCreatedEvent event) {
        log.info("handleUserEvent ****************************************** Message handled " + event);
    }
}
