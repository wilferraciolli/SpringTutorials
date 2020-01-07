package com.wiltech.microserviceone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MicroserviceOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceOneApplication.class, args);
    }

}
