package com.wiltech.rabbitmqconsumer.config;

import static com.wiltech.rabbitmqconsumer.config.RabbitMQConfig.QUEUE_NAME;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.wiltech.rabbitmqconsumer.message.MessageReceived;

@Service
public class CustomMessageReceiver {

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage(final MessageReceived customMessage) {
        System.out.println("Receiving custom message");
        System.out.println(customMessage.getId());
        System.out.println(customMessage.getDescription());
        System.out.println(customMessage.getAmount());
    }
}
