package com.wiltech.rabbitmqconsumer.messages.core;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReceivedRepository extends JpaRepository<MessageReceived, Long> {
}
