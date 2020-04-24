package com.wiltech.rabbitmq.publisher;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiltech.publisher.MessagePublisher;
import com.wiltech.rabbitmq.messages.PersonCreatedEvent;

/**
 * Exemplar to send message to a direct exchange.
 * It requires that the direct exchange exists and that the exchange and key are valid.
 */
@Service
public class RabbitTemplateMessagePublisherForSendingMessageToPersonDirectExchange {

    @Autowired
    private MessagePublisher messagePublisher;

    // send 1 message every second
    @Scheduled(fixedRate = 60000L)
    public void publishMessage() throws JsonProcessingException {

        System.out.println("Publishing message to persons direct exchange");

        sendPersonCreatedMessage();
    }

    private void sendPersonCreatedMessage() throws JsonProcessingException {

        final PersonCreatedEvent event = PersonCreatedEvent.builder()
                .personId(5000L)
                .firstName("wil")
                .lastName("wil")
                .dateOfBirth(LocalDate.of(1985, 02, 16))
                .build();

        messagePublisher.sendMessage(event);
    }

}

