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
@Table(name = "messagesent")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSent implements Serializable {

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

    @Column
    private String source;

    @Column(name = "reply_to")
    private String replyTo;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "event_body")
    private String eventBody;

    @Column(name = "sent_date_time")
    private LocalDateTime sentDateTime;
}
