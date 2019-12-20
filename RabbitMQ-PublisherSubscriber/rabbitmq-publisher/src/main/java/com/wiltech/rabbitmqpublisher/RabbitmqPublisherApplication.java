package com.wiltech.rabbitmqpublisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RabbitmqPublisherApplication {

    public static void main(final String[] args) {
        SpringApplication.run(RabbitmqPublisherApplication.class, args);
    }

}
