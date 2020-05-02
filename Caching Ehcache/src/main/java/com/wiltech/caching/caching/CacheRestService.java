package com.wiltech.caching.caching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API to test caching. It creates a cache with a name and key. The cache is configured on the encache.xml file.
 */
@RestController
@RequestMapping("/experimental")
public class CacheRestService {

    private final static Logger log = LoggerFactory.getLogger(CacheRestService.class);

    @Autowired
    private CacheApplicationService cacheApplicationService;

    @GetMapping("/square/{number}")
    public String testCache(@PathVariable final Long number) {

        log.info("call numberService to square {}", number);

        return String.format("{\"square\": %s}", cacheApplicationService.square(number));
    }
}
