package com.wiltech.edgeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.wiltech.edgeserver.filters.PreZullFilter;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class EdgeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeServerApplication.class, args);
    }


    @Bean
    public PreZullFilter preZullFilter(){

        return new PreZullFilter();
    }
}
