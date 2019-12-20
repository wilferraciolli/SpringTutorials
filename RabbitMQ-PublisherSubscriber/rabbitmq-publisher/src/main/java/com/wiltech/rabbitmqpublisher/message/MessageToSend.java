package com.wiltech.rabbitmqpublisher.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;

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
@JsonRootName("myMessage")
public class MessageToSend implements Serializable {

    private Long Id;
    private String description;
    private double amount;
}
