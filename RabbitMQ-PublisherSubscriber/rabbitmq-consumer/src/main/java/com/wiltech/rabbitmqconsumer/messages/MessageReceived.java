package com.wiltech.rabbitmqconsumer.messages;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@JsonRootName("myMessage")
public class MessageReceived implements Serializable {

    private Long Id;
    private String description;
    private double amount;
}
