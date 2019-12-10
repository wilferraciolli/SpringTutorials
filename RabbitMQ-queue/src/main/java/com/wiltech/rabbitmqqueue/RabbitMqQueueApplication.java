package com.wiltech.rabbitmqqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RabbitMqQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqQueueApplication.class, args);
    }

}
