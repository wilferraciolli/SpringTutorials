package com.wiltech.rabbitmq.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiltech.messaging.CustomMessage;
import com.wiltech.publisher.MessagePublisher;

@Service
public class RabbitTemplateMessagePublisherForTheFanoutExchange {

    @Autowired
    private MessagePublisher messagePublisher;

    @Scheduled(fixedRate = 10000L)
    public void publishMessage() throws JsonProcessingException {

        System.out.println("Publishing custom message to the fanout exchange");

        sendCustomMessage();
    }

    private void sendCustomMessage() throws JsonProcessingException {

        final CustomMessage customMessageToSend = CustomMessage.builder()
                .id(2000L)
                .customDescription("Test")
                .price(4000L)
                .build();

        // will send the message to fan out as the java object sent does not have the @DirectExchange annotation defined.
        messagePublisher.sendMessage(customMessageToSend);
    }

}

