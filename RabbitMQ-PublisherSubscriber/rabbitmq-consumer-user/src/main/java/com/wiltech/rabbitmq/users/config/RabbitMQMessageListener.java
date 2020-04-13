package com.wiltech.rabbitmq.users.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import com.wiltech.core.AbstractMessageReceiver;
import com.wiltech.rabbitmq.users.messages.UserMessageReceiverMapperType;

import lombok.extern.java.Log;

/**
 * Create Default RabbitMQ listener. This class will receive and process each messages, passing the local message types allowed byt this application to the library to map messages onto Domain Events.
 */
@Log
@Component
public class RabbitMQMessageListener extends AbstractMessageReceiver implements MessageListener {

    @Override
    public void onMessage(final Message message) {

        log.info("Message received " + new String(message.getBody()));
        publishDomainEvents(message, new Class[] {UserMessageReceiverMapperType.class});
    }

}
