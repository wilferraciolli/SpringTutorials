package com.wiltech.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigurationProperties(MyConfigProp.class)
public class SpringBootIn3WeeksApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootIn3WeeksApplication.class, args);
	}

}

@RestController
class TestController {

    private final RefreshedService refreshedService;

    private final NonRefreshedService nonRefreshedService;

    private final MyConfigPropService myConfigPropService;

    TestController(RefreshedService refreshedService, NonRefreshedService nonRefreshedService, MyConfigPropService myConfigPropService) {
        this.refreshedService = refreshedService;
        this.nonRefreshedService = nonRefreshedService;
        this.myConfigPropService = myConfigPropService;
    }

    @GetMapping("/refreshed")
    String refreshedService() {
        return refreshedService.test();
    }

    @GetMapping("/nonrefreshed")
    String nonRefreshedService() {
        return nonRefreshedService.test();
    }

    @GetMapping("/configprop")
    String myConfigPropService() {
        return myConfigPropService.test();
    }
}

@RefreshScope
@Service
class RefreshedService {

    private static final Logger log =
            LoggerFactory.getLogger(RefreshedService.class);

    private final String test;

    RefreshedService(@Value("${a:test}") String test) {
        this.test = test;
    }

    String test() {
        log.debug("Hello from refresh scope");
        return test;
    }
}

@Service
class NonRefreshedService {

    private static final Logger log = LoggerFactory.getLogger(NonRefreshedService.class);

    private final String test;

    NonRefreshedService(@Value("${a:test}") String test) {
        this.test = test;
    }

    String test() {
        log.info("Hello from service");
        return test;
    }

}

@Service
class MyConfigPropService {
    private static final Logger log = LoggerFactory.getLogger(MyConfigPropService.class);

    private final MyConfigProp myConfigProp;

    MyConfigPropService(MyConfigProp myConfigProp) {
        this.myConfigProp = myConfigProp;
    }

    String test() {
        log.warn("Hello from config properties");
        return myConfigProp.getProperty();
    }

}

@ConfigurationProperties("config")
class MyConfigProp {
    private String property = "default";

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
