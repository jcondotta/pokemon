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
public class CaffeineCacheServiceConfig {

    @Bean(name = "topRankingCacheService")
    public CacheService<String, List<Pokemon>> topRankingCacheService(@Qualifier("topRankingCache") Cache<String, List<Pokemon>> cache) {
        return new CaffeineCacheService<>(cache);
    }

    @Bean(name = "pokemonCacheService")
    public CacheService<String, Pokemon> pokemonCacheService(@Qualifier("pokemonCache") Cache<String, Pokemon> cache) {
        return new CaffeineCacheService<>(cache);
    }
}