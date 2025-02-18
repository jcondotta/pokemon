package com.jcondotta.pokemon.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache.caffeine")
public record CaffeineCacheProperties(int expireAfterWriteSeconds, int maximumSize) {
}
