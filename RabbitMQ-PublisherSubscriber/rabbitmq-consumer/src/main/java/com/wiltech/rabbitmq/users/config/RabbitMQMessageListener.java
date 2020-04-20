package com.wiltech.rabbitmq.users.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import com.wiltech.core.CustomMessageEventReceiverMapperType;
import com.wiltech.rabbitmq.users.messaging.core.ContextMessageEventReceiverMapperType;
import com.wiltech.receiver.AbstractMessageReceiver;

import lombok.extern.java.Log;

@Log
@Component
public class RabbitMQMessageListener extends AbstractMessageReceiver implements MessageListener {

    @Override
    public void onMessage(final Message message) {

        log.info("Message received " + new String(message.getBody()));

        publishDomainEvents(message, new Class[] {CustomMessageEventReceiverMapperType.class, ContextMessageEventReceiverMapperType.class});

    }

}
