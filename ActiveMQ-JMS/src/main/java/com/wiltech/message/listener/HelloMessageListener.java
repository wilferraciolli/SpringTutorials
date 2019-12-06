/*
 * (c) Midland Software Limited 2019
 * Name     : HelloMessageListener.java
 * Author   : ferraciolliw
 * Date     : 06 Dec 2019
 */
package com.wiltech.message.listener;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.wiltech.message.config.JmsConfig;
import com.wiltech.message.model.HelloWorldMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HelloMessageListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listen(
            @Payload HelloWorldMessage helloWorldMessage,
            @Headers MessageHeaders headers,
            Message message) {

      //  System.out.println("Got the message");

       // System.out.println(helloWorldMessage);

        // throw exception to see the message being bounce back, this allows to test transaction and delivery count by re-delivering.
       // makeMessageToFailAndGoBackToTheQueue();
    }

    //Receive and reply to. This method show how to use spring message as well as JMS.
    // The good part os using Spring message is that it provides an abstraction to JMS which allows easily switching
    @JmsListener(destination = JmsConfig.MY_SEND_RCV_QUEUE)
    public void listenForHello(
            @Payload HelloWorldMessage helloWorldMessage,
            @Headers MessageHeaders headers,
            Message message, org.springframework.messaging.Message springMessage) throws JMSException {

        HelloWorldMessage payloadMsg = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("World!!")
                .build();

        //reply to the replyTo header
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), payloadMsg);

        //example to use Spring Message type
        // jmsTemplate.convertAndSend((Destination) springMessage.getHeaders().get("jms_replyTo"), "got it!");

    }

    private void makeMessageToFailAndGoBackToTheQueue() {
        throw new RuntimeException();
    }
}
