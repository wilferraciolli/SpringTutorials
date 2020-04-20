package com.wiltech.audit;

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

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "received_user_id")
    private String receivedUserId;

    @Column
    private String source;

    @Column(name = "reply_to")
    private String replyTo;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "encoding_type")
    private String encodingType;

    @Column(name = "event_body")
    private String eventBody;

    @Column(name = "received_exchange")
    private String receivedExchange;

    @Column(name = "received_routing_key")
    private String receivedRoutingKey;

    @Column(name = "consumer_tag")
    private String consumerTag;

    @Column(name = "consumer_queue")
    private String consumerQueue;

    @Column(name = "received_date_time")
    private LocalDateTime receivedDateTime;

}
