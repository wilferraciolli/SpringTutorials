package com.wiltech.activemq.messaging.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.wiltech.activemq")
@Import({MessagingConfiguration.class})
public class AppConfig {
}
