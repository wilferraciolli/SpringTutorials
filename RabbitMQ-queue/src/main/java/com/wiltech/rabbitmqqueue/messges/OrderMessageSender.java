package com.wiltech.rabbitmqqueue.messges;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

        //note that on the config there is a default message converter to convert from byte[] to String
        this.rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_ORDERS, order);

        //        This conversion was replaced with some JacksonConverter on the config class
        //        try {
        //            String orderJson = objectMapper.writeValueAsString(order);
        //            Message message = MessageBuilder
        //                    .withBody(orderJson.getBytes())
        //                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
        //                    .build();
        //            this.rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_ORDERS, message);
        //        } catch (JsonProcessingException e) {
        //            e.printStackTrace();
        //        }
    }

    @Scheduled(fixedRate = 5000L)
    public void sendDummyOrderToTheQueue() {
        Order order = Order.builder()
                .productId("product id")
                .orderNumber("order id")
                .amount(100D)
                .build();

        sendOrder(order);
    }
}
