package com.jcondotta.pokemon.infrastructure.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.jcondotta.pokemon.domain.ports.out.CacheService;
import com.jcondotta.pokemon.infrastructure.persistence.cache.CaffeineCacheService;
import com.jcondotta.pokemon.domain.model.Pokemon;
import com.jcondotta.pokemon.service.dto.PokemonRankingDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CaffeineCacheServiceConfig {

    @Bean(name = "pokemonIdsCacheService")
    public CacheService<String, List<Integer>> pokemonIdsCacheService(
            @Qualifier("pokemonIdsCache") Cache<String, List<Integer>> cache) {

        return new CaffeineCacheService<>(cache);
    }

    @Bean(name = "topRankingCacheService")
    public CacheService<String, List<PokemonRankingDTO>> topRankingCacheService(
            @Qualifier("topRankingCache") Cache<String, List<PokemonRankingDTO>> cache) {

        return new CaffeineCacheService<>(cache);
    }

    @Bean(name = "pokemonCacheService")
    public CacheService<String, Pokemon> pokemonCacheService(
            @Qualifier("pokemonCache") Cache<String, Pokemon> cache) {

        return new CaffeineCacheService<>(cache);
    }
}