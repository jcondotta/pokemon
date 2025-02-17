package com.jcondotta.pokemon.service;

import com.jcondotta.pokemon.model.Pokemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ConcurrentPokemonDetailFetcherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentPokemonDetailFetcherService.class);

    private final PokemonDetailFetcherService pokemonDetailFetcherService;
    private final ExecutorService executorService;

    public ConcurrentPokemonDetailFetcherService(PokemonDetailFetcherService pokemonDetailFetcherService,
                                                 @Value("${pokemon.api.fetch-by-id.thread-pool-size}") int threadPoolSize) {
        this.pokemonDetailFetcherService = pokemonDetailFetcherService;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public List<Pokemon> fetchPokemonListConcurrently(Collection<Integer> pokemonIds) {
        Instant startTime = Instant.now();
        LOGGER.info("Starting concurrent fetch for {} Pokémon...", pokemonIds.size());

        List<CompletableFuture<Optional<Pokemon>>> futures = pokemonIds.stream()
                .map(this::fetchPokemonAsync)
                .toList();

        List<Pokemon> results = futures.stream()
                .map(CompletableFuture::join) // Wait for each future
                .flatMap(Optional::stream)    // Filter out empty Optionals
                .collect(Collectors.toList());

        long totalTimeMs = Duration.between(startTime, Instant.now()).toMillis();
        LOGGER.info("Concurrent fetch completed in {} ms. Fetched {} Pokémon.", totalTimeMs, results.size());

        return results;
    }

    private CompletableFuture<Optional<Pokemon>> fetchPokemonAsync(Integer pokemonId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return pokemonDetailFetcherService.fetchById(pokemonId);
            }
            catch (Exception e) {
                LOGGER.error("Failed to fetch Pokémon ID: {} - {}", pokemonId, e.getMessage());
                return Optional.empty();
            }
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
