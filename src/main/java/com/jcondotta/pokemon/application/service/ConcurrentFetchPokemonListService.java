package com.jcondotta.pokemon.application.service;

import com.jcondotta.pokemon.application.ports.out.api.PokemonFetchDetailsPort;
import com.jcondotta.pokemon.application.usecases.FetchPokemonListUseCase;
import com.jcondotta.pokemon.domain.model.Pokemon;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ConcurrentFetchPokemonListService implements FetchPokemonListUseCase, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentFetchPokemonListService.class);

    private final PokemonFetchDetailsPort pokemonFetchDetailsPort;
    private final ExecutorService executorService;

    public ConcurrentFetchPokemonListService(PokemonFetchDetailsPort pokemonFetchDetailsPort,
                                             @Value("${pokemon.api.fetch-by-id.thread-pool-size}") int threadPoolSize) {
        this.pokemonFetchDetailsPort = pokemonFetchDetailsPort;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    @Override
    public List<Pokemon> fetchPokemonList(Collection<Integer> pokemonIds) {
        Instant startTime = Instant.now();
        LOGGER.info("Starting concurrent fetch for {} Pokémon...", pokemonIds.size());

        List<CompletableFuture<Optional<Pokemon>>> futures = pokemonIds.stream()
                .map(this::fetchPokemonAsync)
                .toList();

        List<Pokemon> results = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        long totalTimeMs = Duration.between(startTime, Instant.now()).toMillis();
        LOGGER.info("Concurrent fetch completed in {} ms. Fetched {} Pokémon.", totalTimeMs, results.size());

        return results;
    }

    private CompletableFuture<Optional<Pokemon>> fetchPokemonAsync(Integer pokemonId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return pokemonFetchDetailsPort.fetchById(pokemonId);
            }
            catch (Exception e) {
                LOGGER.error("Failed to fetch Pokémon ID: {} - {}", pokemonId, e.getMessage());
                return Optional.empty();
            }
        }, executorService);
    }

    @Override
    public void close() {
        LOGGER.info("Shutting down ExecutorService...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                LOGGER.warn("Forcing shutdown of ExecutorService...");
                executorService.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            LOGGER.error("ExecutorService shutdown interrupted: {}", e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

