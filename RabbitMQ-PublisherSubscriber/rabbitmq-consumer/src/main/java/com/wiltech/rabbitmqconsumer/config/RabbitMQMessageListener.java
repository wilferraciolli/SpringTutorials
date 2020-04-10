package com.wiltech.rabbitmqconsumer.config;

import java.util.Objects;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import com.wiltech.core.AbstractMessageReceiver;
import com.wiltech.core.MessageEvent;
import com.wiltech.rabbitmqconsumer.messages.core.MessageReceiverType;

import lombok.extern.java.Log;

@Log
@Component
public class RabbitMQMessageListener extends AbstractMessageReceiver implements MessageListener {

    @Override
    public void onMessage(final Message message) {

        log.info("Message received " + new String(message.getBody()));

        if (Objects.nonNull(message.getMessageProperties()) && message.getMessageProperties().getType() != null) {
            this.publishDomainEvents(message, new Class[] {MessageReceiverType.class});

        } else {
            log.warning("Message properties type cannot be null");
        }
    }

    @Override
    protected <T extends Enum<T> & MessageEvent> void publishDomainEvents(Message message, Class<T>... msgEventClass) {
        super.publishDomainEvents(message, msgEventClass);
    }
}
