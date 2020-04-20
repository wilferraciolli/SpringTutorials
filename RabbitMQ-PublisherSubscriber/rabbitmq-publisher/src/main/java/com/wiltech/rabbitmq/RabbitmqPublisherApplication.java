package com.wiltech.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.wiltech")
@EnableJpaRepositories("com.wiltech")
@EntityScan("com.wiltech")
public class RabbitmqPublisherApplication {

    public static void main(final String[] args) {
        SpringApplication.run(RabbitmqPublisherApplication.class, args);
    }

}
