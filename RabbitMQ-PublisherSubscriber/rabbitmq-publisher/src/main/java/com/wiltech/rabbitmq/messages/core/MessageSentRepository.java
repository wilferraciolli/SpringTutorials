package com.wiltech.rabbitmq.messages.core;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageSentRepository extends JpaRepository<MessageSent, Long> {
}
