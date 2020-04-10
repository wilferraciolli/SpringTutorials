package com.wiltech.rabbitmqconsumer.config;

import java.util.List;
import java.util.Objects;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiltech.rabbitmqconsumer.bean.initializer.AppContextBeanUtil;
import com.wiltech.rabbitmqconsumer.messages.core.AbstractMessageReceiver;
import com.wiltech.rabbitmqconsumer.messages.core.DomainEvent;
import com.wiltech.rabbitmqconsumer.messages.core.EventPublisher;
import com.wiltech.rabbitmqconsumer.messages.core.MessageReceiverType;

import lombok.extern.java.Log;

@Log
@Component
public class RabbitMQMessageListener extends AbstractMessageReceiver implements MessageListener {

    @Override
    public void onMessage(final Message message) {

        log.info("Message received " + new String(message.getBody()));

        if (Objects.nonNull(message.getMessageProperties()) && message.getMessageProperties().getType() != null) {
            publishDomainEvents(message);

        } else {
            log.warning("Message properties type cannot be null");
        }
    }

    private void publishDomainEvents(Message message) {

        try {
            List<DomainEvent> messages = processQueueMessage(message, new Class[] {MessageReceiverType.class});
            messages.stream()
                    .forEach(event -> {
                        log.fine("The event is " + event);
                        AppContextBeanUtil.getBean(EventPublisher.class).publishEvent(event);
                    });

        } catch (JsonProcessingException e) {
            log.severe("Could not find an event to deserialize the message " + e.getMessage());
        }
    }

}
