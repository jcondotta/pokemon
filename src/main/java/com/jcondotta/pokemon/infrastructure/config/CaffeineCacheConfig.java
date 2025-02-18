package com.jcondotta.pokemon.infrastructure.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jcondotta.pokemon.domain.model.Pokemon;
import com.jcondotta.pokemon.service.dto.PokemonRankingDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CaffeineCacheConfig {

    private final CaffeineCacheProperties properties;

    public CaffeineCacheConfig(CaffeineCacheProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "pokemonIdsCache")
    public Cache<String, List<Integer>> pokemonIdsCache() {
        return Caffeine.newBuilder()
//                .expireAfterWrite(properties.expireAfterWriteSeconds(), TimeUnit.SECONDS)
//                .maximumSize(properties.maximumSize())
                .build();
    }

    @Bean(name = "topRankingCache")
    public Cache<String, List<PokemonRankingDTO>> topRankingCache() {
        return Caffeine.newBuilder()
//                .expireAfterWrite(properties.expireAfterWriteSeconds(), TimeUnit.SECONDS)
//                .maximumSize(properties.maximumSize())
                .build();
    }

    @Bean(name = "pokemonCache")
    public Cache<String, Pokemon> pokemonCache() {
        return Caffeine.newBuilder()
//                .expireAfterWrite(properties.expireAfterWriteSeconds(), TimeUnit.SECONDS)
//                .maximumSize(properties.maximumSize())
                .build(); //TODO ajustar o TTL
    }
}