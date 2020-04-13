package com.wiltech.rabbitmq.users.messages;

import java.util.ArrayList;
import java.util.List;

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
@JsonRootName("userCreatedEvent")
public class UserCreatedEvent implements DomainEvent {

    private Long userId;
    private String email;
    private List<String> roles = new ArrayList<>();

}
