package com.wiltech.rabbitmq.users.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import com.wiltech.core.CustomMessageEventReceiverMapperType;
import com.wiltech.rabbitmq.users.messaging.events.PersonMessageReceiverMapperType;
import com.wiltech.receiver.AbstractMessageReceiver;

import lombok.extern.java.Log;

/**
 * Create Default RabbitMQ listener. This class will receive and process each messages, passing the local message types allowed byt this application to the library to map messages onto Domain Events.
 */
@Log
@Component
public class AMQPFanoutMessageListener extends AbstractMessageReceiver implements MessageListener {

    @Override
    public void onMessage(final Message message) {

        log.info("Message received " + new String(message.getBody()));

        // add the custom and person message to simulate multiple event type
        publishDomainEvents(message, new Class[] {CustomMessageEventReceiverMapperType.class, PersonMessageReceiverMapperType.class});
    }

}
