package com.jcondotta.pokemon.infrastructure.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jcondotta.pokemon.application.usecases.dto.PokemonRankingDTO;
import com.jcondotta.pokemon.domain.model.Pokemon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CaffeineCacheConfig {

    @Bean(name = "pokemonIdsCache")
    public Cache<String, List<Integer>> pokemonIdsCache() {
        return Caffeine.newBuilder()
                .build();
    }

    @Bean(name = "topRankingCache")
    public Cache<String, List<PokemonRankingDTO>> topRankingCache() {
        return Caffeine.newBuilder()
                .build();
    }

    @Bean(name = "pokemonCache")
    public Cache<String, Pokemon> pokemonCache() {
        return Caffeine.newBuilder()
                .build();
    }
}