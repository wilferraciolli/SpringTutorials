package com.wiltech.rabbitmqqueue.messges;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wiltech.rabbitmqqueue.config.RabbitConfig;

/**
 * The type Order message sender.
 */
@Service
public class OrderMessageSender {
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
        this.rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_ORDERS, order);
    }
}
