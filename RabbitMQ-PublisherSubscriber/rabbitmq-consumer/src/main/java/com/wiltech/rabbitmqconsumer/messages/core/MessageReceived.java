package com.wiltech.rabbitmqconsumer.messages.core;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messagereceived")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReceived implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column
    private String source;

    @Column(name = "reply_to")
    private String replyTo;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "message_body")
    private String messageBody;

    @Column(name = "received_date_time")
    private LocalDateTime receivedDateTime;

}
