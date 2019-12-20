package com.wiltech.rabbitmqconsumer.config;

import static com.wiltech.rabbitmqconsumer.config.RabbitMQConfig.QUEUE_NAME;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.wiltech.rabbitmqconsumer.message.MessageReceived;

public class RabbitMQMessageListener implements MessageListener {

    @Override
    public void onMessage(final Message message) {

        System.out.println("Message received " + new String(message.getBody()));
    }

}
