package com.riya.smarttravel.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "weather",
                "forecast",
                "holidays",
                "upcoming-holidays",
                "long-weekends",
                "geocode",
                "reverse-geocode",
                "place-search",
                "travel-images",
                "city-images",
                "place-images"
        );
    }
}