package com.wiltech.rabbitmq.publisher;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiltech.publisher.MessagePublisher;
import com.wiltech.rabbitmq.messages.UserCreatedEvent;

/**
 * Exemplar to send message to a direct exchange.
 * It requires that the direct exchange exists and that the exchange and key are valid.
 */
@Service
public class RabbitTemplateMessagePublisherForSendingMessageToUsersDirectExchange {

    @Autowired
    private MessagePublisher messagePublisher;

    // send 1 message every second
    @Scheduled(fixedRate = 60000L)
    public void publishMessage() throws JsonProcessingException {

        System.out.println("Publishing message to users direct exchange");

        sendUserCreatedMessage();
    }

    private void sendUserCreatedMessage() throws JsonProcessingException {

        final UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(2000L)
                .email("email@email.com")
                .roles(Arrays.asList("ADMIN", "USER"))
                .build();

        messagePublisher.sendMessage(event);
    }
}

