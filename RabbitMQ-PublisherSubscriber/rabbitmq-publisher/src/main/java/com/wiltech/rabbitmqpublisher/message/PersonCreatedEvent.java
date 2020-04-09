package com.wiltech.rabbitmqpublisher.message;

import java.time.LocalDate;

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
@JsonRootName("personCreatedEvent")
public class PersonCreatedEvent {

    private Long personId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

}
