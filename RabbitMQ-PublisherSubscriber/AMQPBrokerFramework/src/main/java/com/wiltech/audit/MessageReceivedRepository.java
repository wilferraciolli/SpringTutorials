package com.wiltech.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReceivedRepository extends JpaRepository<MessageReceived, Long> {
}
