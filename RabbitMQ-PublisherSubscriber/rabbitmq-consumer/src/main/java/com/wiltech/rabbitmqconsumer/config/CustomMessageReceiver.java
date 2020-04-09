package com.wiltech.rabbitmqconsumer.config;

import org.springframework.stereotype.Service;

@Service
public class CustomMessageReceiver {

    //    @RabbitListener(queues = QUEUE_NAME)
    //    public void receiveMessage(final MessageReceived customMessage) {
    //        System.out.println("Receiving custom message");
    //        System.out.println(customMessage.getId());
    //        System.out.println(customMessage.getDescription());
    //        System.out.println(customMessage.getAmount());
    //    }
}
