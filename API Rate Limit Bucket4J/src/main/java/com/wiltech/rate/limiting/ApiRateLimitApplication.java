package com.wiltech.rate.limiting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ApiRateLimitApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiRateLimitApplication.class, args);
    }

}
