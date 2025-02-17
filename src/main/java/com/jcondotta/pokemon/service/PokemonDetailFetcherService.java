package com.jcondotta.pokemon.service;

import com.jcondotta.pokemon.cache.CacheService;
import com.jcondotta.pokemon.cache.PokemonCacheKey;
import com.jcondotta.pokemon.model.Pokemon;
import com.jcondotta.pokemon.service.client.PokemonDetailAPIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PokemonDetailFetcherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonDetailFetcherService.class);

    private final CacheService<String, Pokemon> cacheService;
    private final PokemonDetailAPIClient pokemonDetailAPIClient;

    public PokemonDetailFetcherService(@Qualifier("pokemonCacheService") CacheService<String, Pokemon> cacheService, PokemonDetailAPIClient pokemonDetailAPIClient) {
        this.cacheService = cacheService;
        this.pokemonDetailAPIClient = pokemonDetailAPIClient;
    }

    public Optional<Pokemon> fetchById(int pokemonId) {
        var cacheKey = PokemonCacheKey.POKEMON_DETAILS.format(pokemonId);

        var startTime = Instant.now();

        LOGGER.info("Pokémon ID: {} - Checking cache...", pokemonId);
        var pokemon = cacheService.getOrFetch(cacheKey, key -> pokemonDetailAPIClient.fetchById(pokemonId));

        var totalElapsedTimeMs = Duration.between(startTime, Instant.now()).toMillis();
        LOGGER.info("Pokémon ID: {} - Fetch completed in ({} ms)", pokemonId, totalElapsedTimeMs);

        return pokemon;
    }
}