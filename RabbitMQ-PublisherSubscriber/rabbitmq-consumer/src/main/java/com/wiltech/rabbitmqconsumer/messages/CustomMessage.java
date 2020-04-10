package com.wiltech.rabbitmqconsumer.messages;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.core.DomainEvent;

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
@JsonRootName("myCustomMessage")
public class CustomMessage implements DomainEvent {

    private Long id;
    private String customDescription;
    private double price;
}
