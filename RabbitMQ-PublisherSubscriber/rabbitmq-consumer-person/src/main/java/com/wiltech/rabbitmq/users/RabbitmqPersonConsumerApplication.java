package com.wiltech.rabbitmq.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.wiltech")
@EnableJpaRepositories("com.wiltech")
@EntityScan("com.wiltech")
public class RabbitmqPersonConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqPersonConsumerApplication.class, args);
    }

}
