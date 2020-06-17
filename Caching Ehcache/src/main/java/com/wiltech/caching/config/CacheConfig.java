/*
 * (c) Midland Software Limited 2020
 * Name     : CacheConfig.java
 * Author   : ferraciolliw
 * Date     : 02 May 2020
 */
package com.wiltech.caching.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Class to enable Cache so that Spring's annotation-driven cache management is enabled.
 */
@Configuration
@EnableCaching
public class CacheConfig {

}
