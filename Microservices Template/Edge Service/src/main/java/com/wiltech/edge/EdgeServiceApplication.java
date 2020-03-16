package com.wiltech.edge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.wiltech.edge.filter.GatewayFilter;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
@ComponentScan("com.wiltech")
public class EdgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeServiceApplication.class, args);
    }

    @Bean
    public GatewayFilter preZuulFilter(){

        return new GatewayFilter();
    }
}
