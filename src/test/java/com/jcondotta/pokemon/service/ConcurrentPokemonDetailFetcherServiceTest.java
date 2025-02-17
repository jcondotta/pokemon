package com.jcondotta.pokemon.service;

import com.jcondotta.pokemon.helper.TestPokemon;
import com.jcondotta.pokemon.model.Pokemon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcurrentPokemonDetailFetcherServiceTest {

    private static final int THREAD_POOL_SIZE = 4;

    @Mock
    private PokemonDetailFetcherService pokemonDetailFetcherService;

    private ConcurrentPokemonDetailFetcherService concurrentPokemonDetailFetcherService;

    private static final Map<Integer, Pokemon> mapPokemonById = Arrays.stream(TestPokemon.values())
            .collect(Collectors.toMap(TestPokemon::getId, TestPokemon::pokemonDetailsToPokemon));

    @BeforeEach
    void beforeEach() {
        concurrentPokemonDetailFetcherService = new ConcurrentPokemonDetailFetcherService(pokemonDetailFetcherService, THREAD_POOL_SIZE);
    }

    @AfterEach
    void afterEach() {
        concurrentPokemonDetailFetcherService.shutdown();
    }

    @Test
    void shouldFetchPokemonSuccessfully_whenFetchingConcurrently() {
        mapPokemonById.values().forEach(testPokemon ->
                        when(pokemonDetailFetcherService.fetchById(testPokemon.id()))
                                .thenReturn(Optional.of(testPokemon)));

        var fetchedPokemonList = concurrentPokemonDetailFetcherService.fetchPokemonListConcurrently(mapPokemonById.keySet());

        assertThat(fetchedPokemonList)
                .hasSize(mapPokemonById.size())
                .allSatisfy(pokemon -> assertThat(pokemon)
                        .usingRecursiveComparison()
                        .isEqualTo(mapPokemonById.get(pokemon.id())));
    }

    @Test
    void shouldHandlePartialFailures_whenFetchingConcurrently() {
        int charizardFailingPokemonId = TestPokemon.CHARIZARD.getId();

        mapPokemonById.values().forEach(testPokemon -> {
            if (testPokemon.id() == charizardFailingPokemonId) {
                when(pokemonDetailFetcherService.fetchById(charizardFailingPokemonId))
                        .thenThrow(new RuntimeException());
            }
            else {
                when(pokemonDetailFetcherService.fetchById(testPokemon.id())).thenReturn(Optional.of(testPokemon));
            }
        });

        var fetchedPokemonList = concurrentPokemonDetailFetcherService.fetchPokemonListConcurrently(mapPokemonById.keySet());

        assertThat(fetchedPokemonList)
                .hasSize(mapPokemonById.size() - 1)
                .allSatisfy(pokemon -> assertThat(pokemon)
                        .usingRecursiveComparison()
                        .isEqualTo(mapPokemonById.get(pokemon.id())));

        verify(pokemonDetailFetcherService).fetchById(charizardFailingPokemonId);
    }

    @Test
    void shouldHandleMultipleFailures_whenFetchingConcurrently() {
        int halfSize = mapPokemonById.size() / 2;

        var failingIds = mapPokemonById.keySet().stream()
                .limit(halfSize)
                .collect(Collectors.toSet());

        var successfulIds = mapPokemonById.keySet().stream()
                .skip(halfSize)
                .collect(Collectors.toSet());

        mapPokemonById.forEach((id, testPokemon) -> {
            when(pokemonDetailFetcherService.fetchById(id))
                    .thenReturn(failingIds.contains(id) ? Optional.empty() : Optional.of(testPokemon));
        });

        var fetchedPokemonList = concurrentPokemonDetailFetcherService.fetchPokemonListConcurrently(mapPokemonById.keySet());

        assertThat(fetchedPokemonList)
                .hasSize(successfulIds.size())
                .allSatisfy(pokemon -> assertThat(pokemon)
                        .usingRecursiveComparison()
                        .isEqualTo(mapPokemonById.get(pokemon.id())));

        mapPokemonById.keySet()
                .forEach(id -> verify(pokemonDetailFetcherService).fetchById(id));
    }

    @Test
    void shouldReturnEmptyList_whenNoPokemonIdsProvided() {
        Set<Integer> emptyPokemonIds = Collections.emptySet();

        var fetchedPokemonList = concurrentPokemonDetailFetcherService.fetchPokemonListConcurrently(emptyPokemonIds);

        assertThat(fetchedPokemonList)
                .isNotNull()
                .isEmpty();

        verifyNoInteractions(pokemonDetailFetcherService);
    }
}
