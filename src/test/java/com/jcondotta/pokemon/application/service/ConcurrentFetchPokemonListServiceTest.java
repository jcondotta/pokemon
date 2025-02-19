package com.jcondotta.pokemon.application.service;

import com.jcondotta.pokemon.application.ports.out.api.PokemonFetchDetailsPort;
import com.jcondotta.pokemon.application.usecases.FetchPokemonListUseCase;
import com.jcondotta.pokemon.domain.model.Pokemon;
import com.jcondotta.pokemon.helper.TestPokemon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcurrentFetchPokemonListServiceTest {

    private static final int THREAD_POOL_SIZE = 4;

    @Mock
    private PokemonFetchDetailsPort pokemonFetchDetailsPort;

    private FetchPokemonListUseCase fetchPokemonListUseCase;

    private static final Map<Integer, Pokemon> mapPokemonById = Arrays.stream(TestPokemon.values())
            .collect(Collectors.toMap(TestPokemon::getId, TestPokemon::pokemonDetailsToPokemon));

    @BeforeEach
    void beforeEach() {
        fetchPokemonListUseCase = new ConcurrentFetchPokemonListService(pokemonFetchDetailsPort, THREAD_POOL_SIZE);
    }

    @Test
    void shouldFetchPokemonSuccessfully_whenFetchingConcurrently() {
        mapPokemonById.values().forEach(testPokemon ->
                        when(pokemonFetchDetailsPort.fetchById(testPokemon.id()))
                                .thenReturn(Optional.of(testPokemon)));

        var fetchedPokemonList = fetchPokemonListUseCase.fetchPokemonList(mapPokemonById.keySet());

        assertThat(fetchedPokemonList)
                .hasSize(mapPokemonById.size())
                .allSatisfy(pokemon -> assertThat(pokemon)
                        .usingRecursiveComparison()
                        .isEqualTo(mapPokemonById.get(pokemon.id())));

        mapPokemonById.keySet()
                .forEach(pokemonId -> verify(pokemonFetchDetailsPort).fetchById(pokemonId));

    }

    @Test
    void shouldHandlePartialFailures_whenFetchingConcurrently() {
        int charizardFailingPokemonId = TestPokemon.CHARIZARD.getId();

        mapPokemonById.values().forEach(testPokemon -> {
            if (testPokemon.id() == charizardFailingPokemonId) {
                when(pokemonFetchDetailsPort.fetchById(charizardFailingPokemonId))
                        .thenThrow(new RuntimeException());
            }
            else {
                when(pokemonFetchDetailsPort.fetchById(testPokemon.id())).thenReturn(Optional.of(testPokemon));
            }
        });

        var fetchedPokemonList = fetchPokemonListUseCase.fetchPokemonList(mapPokemonById.keySet());

        assertThat(fetchedPokemonList)
                .hasSize(mapPokemonById.size() - 1)
                .allSatisfy(pokemon -> assertThat(pokemon)
                        .usingRecursiveComparison()
                        .isEqualTo(mapPokemonById.get(pokemon.id())));

        mapPokemonById.keySet()
                .forEach(pokemonId -> verify(pokemonFetchDetailsPort).fetchById(pokemonId));
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
            when(pokemonFetchDetailsPort.fetchById(id))
                    .thenReturn(failingIds.contains(id) ? Optional.empty() : Optional.of(testPokemon));
        });

        var fetchedPokemonList = fetchPokemonListUseCase.fetchPokemonList(mapPokemonById.keySet());

        assertThat(fetchedPokemonList)
                .hasSize(successfulIds.size())
                .allSatisfy(pokemon -> assertThat(pokemon)
                        .usingRecursiveComparison()
                        .isEqualTo(mapPokemonById.get(pokemon.id())));

        mapPokemonById.keySet()
                .forEach(id -> verify(pokemonFetchDetailsPort).fetchById(id));
    }

    @Test
    void shouldReturnEmptyList_whenNoPokemonIdsProvided() {
        Set<Integer> emptyPokemonIds = Collections.emptySet();

        var fetchedPokemonList = fetchPokemonListUseCase.fetchPokemonList(emptyPokemonIds);

        assertThat(fetchedPokemonList)
                .isNotNull()
                .isEmpty();

        verifyNoInteractions(pokemonFetchDetailsPort);
    }
}
