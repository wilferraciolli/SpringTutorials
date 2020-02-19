package com.wiltech.courses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

import com.wiltech.core.properties.JwtConfiguration;

@SpringBootApplication
@ComponentScan("com.wiltech")
@EnableConfigurationProperties(value = JwtConfiguration.class)
@EnableEurekaClient
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }

}
