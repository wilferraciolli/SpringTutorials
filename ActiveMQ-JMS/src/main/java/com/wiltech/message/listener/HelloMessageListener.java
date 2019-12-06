/*
 * (c) Midland Software Limited 2019
 * Name     : HelloMessageListener.java
 * Author   : ferraciolliw
 * Date     : 06 Dec 2019
 */
package com.wiltech.message.listener;

import javax.jms.Message;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.wiltech.message.config.JmsConfig;
import com.wiltech.message.model.HelloWorldMessage;

@Component
public class HelloMessageListener {

    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listen(
            @Payload HelloWorldMessage helloWorldMessage,
            @Headers MessageHeaders headers,
            Message message) {

        System.out.println("Got the message");

        System.out.println(helloWorldMessage);
    }
}
