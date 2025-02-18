package com.jcondotta.pokemon.application.service;

import com.jcondotta.pokemon.domain.model.Pokemon;
import com.jcondotta.pokemon.domain.ports.in.PokemonFetchDetailsUseCase;
import com.jcondotta.pokemon.domain.ports.out.CacheService;
import com.jcondotta.pokemon.domain.ports.out.PokemonFetchDetailsPort;
import com.jcondotta.pokemon.infrastructure.persistence.cache.PokemonCacheKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PokemonDetailsFetcherService implements PokemonFetchDetailsUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonDetailsFetcherService.class);

    private final CacheService<String, Pokemon> cacheService;
    private final PokemonFetchDetailsPort pokemonFetchDetailsPort;

    public PokemonDetailsFetcherService(CacheService<String, Pokemon> cacheService, PokemonFetchDetailsPort pokemonFetchDetailsPort) {
        this.cacheService = cacheService;
        this.pokemonFetchDetailsPort = pokemonFetchDetailsPort;
    }

    @Override
    public Optional<Pokemon> fetchById(int pokemonId) {
        var cacheKey = PokemonCacheKeys.pokemonDetails(pokemonId);
        var startTime = Instant.now();

        LOGGER.info("Pokémon ID: {} - Checking cache...", pokemonId);
        var pokemon = cacheService.getOrFetch(cacheKey, key -> pokemonFetchDetailsPort.fetchById(pokemonId));

        var totalElapsedTimeMs = Duration.between(startTime, Instant.now()).toMillis();
        LOGGER.info("Pokémon ID: {} - Fetch completed in ({} ms)", pokemonId, totalElapsedTimeMs);

        return pokemon;
    }
}