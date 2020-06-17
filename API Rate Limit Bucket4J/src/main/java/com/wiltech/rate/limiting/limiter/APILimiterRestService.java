package com.wiltech.rate.limiting.limiter;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/experimental")
public class APILimiterRestService {

    private final static Logger log = LoggerFactory.getLogger(APILimiterRestService.class);

    @GetMapping("/test")
    public String testAPILimit() {

        log.info("Called REST " + Instant.now());

        return "test";
    }
}
