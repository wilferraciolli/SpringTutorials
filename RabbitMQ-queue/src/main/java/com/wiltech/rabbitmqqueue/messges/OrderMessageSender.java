package com.wiltech.rabbitmqqueue.messges;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiltech.rabbitmqqueue.config.RabbitConfig;

/**
 * The type Order message sender.
 */
@Service
public class OrderMessageSender {

    @Autowired
    private ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;

    /**
     * Instantiates a new Order message sender.
     * @param rabbitTemplate the rabbit template
     */
    @Autowired
    public OrderMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Send order.
     * By default Spring Boot uses org.springframework.amqp.support.converter.SimpleMessageConverter and serialize the object into byte[]
     * @param order the order
     */
    public void sendOrder(Order order) {
       // send as byte[] this.rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_ORDERS, order);

        try {
            String orderJson = objectMapper.writeValueAsString(order);
            Message message = MessageBuilder
                    .withBody(orderJson.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();
            this.rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_ORDERS, message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
