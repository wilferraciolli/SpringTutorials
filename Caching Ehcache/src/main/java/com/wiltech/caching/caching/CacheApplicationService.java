package com.wiltech.caching.caching;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service to implement caching. This demonstrate how a conditional caching can be implemented.
 */
@Service
public class CacheApplicationService {

    private final static Logger log = LoggerFactory.getLogger(CacheApplicationService.class);

    @Cacheable(value = "squareCache", key = "#number", condition = "#number>10")
    public BigDecimal square(final Long number) {
        final BigDecimal square = BigDecimal.valueOf(number)
                .multiply(BigDecimal.valueOf(number));
        log.info("square of {} is {}", number, square);

        return square;
    }
}
