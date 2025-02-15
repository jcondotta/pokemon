package com.jcondotta.pokemon.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jcondotta.pokemon.cache.CacheService;
import com.jcondotta.pokemon.cache.caffeine.CaffeineCacheService;
import com.jcondotta.pokemon.model.Pokemon;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    private final CaffeineCacheProperties properties;

    public CaffeineCacheConfig(CaffeineCacheProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "topRankingCache")
    public Cache<String, List<Pokemon>> topRankingCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(properties.expireAfterWriteSeconds(), TimeUnit.SECONDS)
                .maximumSize(properties.maximumSize())
                .build();
    }

    @Bean(name = "pokemonCache")
    public Cache<String, Pokemon> pokemonCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(properties.expireAfterWriteSeconds(), TimeUnit.SECONDS)
                .maximumSize(properties.maximumSize())
                .build();
    }
}