package com.wiltech.rabbitmqconsumer.config;

import java.util.Objects;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import com.wiltech.rabbitmqconsumer.messages.core.AbstractMessageReceiver;

import lombok.extern.java.Log;

@Log
@Component
public class RabbitMQMessageListener extends AbstractMessageReceiver implements MessageListener {

    @Override
    public void onMessage(final Message message) {

        log.info("Message received " + new String(message.getBody()));

        if (Objects.nonNull(message.getMessageProperties()) && message.getMessageProperties().getType() != null) {
            //            createRecordForMessageReceived(message);
            //            publishDomainEvents(message);
            publishDomainEvents(message);

        } else {
            log.warning("Message properties type cannot be null");
        }
    }

}
