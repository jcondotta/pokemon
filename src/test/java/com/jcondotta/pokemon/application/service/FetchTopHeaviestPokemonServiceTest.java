package com.jcondotta.pokemon.application.service;

import com.jcondotta.pokemon.application.ports.out.cache.CacheService;
import com.jcondotta.pokemon.application.usecases.FetchAllPokemonIdsUseCase;
import com.jcondotta.pokemon.application.usecases.FetchPokemonListUseCase;
import com.jcondotta.pokemon.application.usecases.FetchTopRankedPokemonUseCase;
import com.jcondotta.pokemon.application.usecases.dto.PokemonRankingDTO;
import com.jcondotta.pokemon.domain.model.Pokemon;
import com.jcondotta.pokemon.helper.TestPokemon;
import com.jcondotta.pokemon.infrastructure.adapters.out.cache.PokemonCacheKeys;
import com.jcondotta.pokemon.infrastructure.adapters.out.cache.PokemonRankingType;
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
class FetchTopHeaviestPokemonServiceTest {

    private static final int TOP_N = 5;
    private static final String TOP_HEAVIEST_CACHE_KEY = PokemonCacheKeys.topRanking(PokemonRankingType.HEAVIEST, TOP_N);

    @Mock
    private FetchAllPokemonIdsUseCase fetchAllPokemonIdsUseCase;

    @Mock
    private FetchPokemonListUseCase fetchPokemonListUseCase;

    @Mock
    private CacheService<String, List<PokemonRankingDTO>> cacheService;

    private FetchTopRankedPokemonUseCase fetchTopHeaviestPokemonUseCase;

    @BeforeEach
    void beforeEach() {
        fetchTopHeaviestPokemonUseCase = new FetchTopHeaviestPokemonService(fetchAllPokemonIdsUseCase, fetchPokemonListUseCase, cacheService);
    }

    private final Map<Integer, Pokemon> pokemonMap = Arrays.stream(TestPokemon.values())
            .collect(Collectors.toMap(TestPokemon::getId, TestPokemon::pokemonDetailsToPokemon));

    private final List<PokemonRankingDTO> expectedTopHeaviest = pokemonMap.values().stream()
            .sorted(Comparator.comparingDouble(Pokemon::weight).reversed())
            .limit(TOP_N)
            .map(pokemon -> new PokemonRankingDTO(pokemon.name(), pokemon.weight()))
            .toList();

    @Test
    void shouldReturnCachedValue_whenCacheHitOccurs() {
        when(fetchAllPokemonIdsUseCase.fetchAllPokemonIds()).thenReturn(pokemonMap.keySet());
        when(cacheService.get(TOP_HEAVIEST_CACHE_KEY)).thenReturn(Optional.of(expectedTopHeaviest));

        var topHeaviestPokemon = fetchTopHeaviestPokemonUseCase.getTopRankedPokemon(TOP_N);

        assertThat(topHeaviestPokemon)
                .hasSize(TOP_N)
                .containsExactlyElementsOf(expectedTopHeaviest);

        verify(cacheService).get(TOP_HEAVIEST_CACHE_KEY);
        verifyNoInteractions(fetchPokemonListUseCase);
        verify(cacheService, never()).set(anyString(), any());
    }

    @Test
    void shouldFetchAndCache_whenCacheMissOccurs() {
        when(fetchAllPokemonIdsUseCase.fetchAllPokemonIds()).thenReturn(pokemonMap.keySet());
        when(fetchPokemonListUseCase.fetchPokemonList(any()))
                .thenReturn(pokemonMap.values().stream().toList());

        var topHeaviestPokemon = fetchTopHeaviestPokemonUseCase.getTopRankedPokemon(TOP_N);

        assertThat(topHeaviestPokemon)
                .hasSize(TOP_N)
                .containsExactlyElementsOf(expectedTopHeaviest);

        verify(cacheService).get(TOP_HEAVIEST_CACHE_KEY);
        verify(fetchPokemonListUseCase).fetchPokemonList(pokemonMap.keySet());
        verify(cacheService).set(TOP_HEAVIEST_CACHE_KEY, expectedTopHeaviest);
    }

    @Test
    void shouldReturnEmptyList_whenNoPokemonAreFetched() {
        when(fetchAllPokemonIdsUseCase.fetchAllPokemonIds()).thenReturn(List.of());

        var topHeaviestPokemon = fetchTopHeaviestPokemonUseCase.getTopRankedPokemon(TOP_N);
        assertThat(topHeaviestPokemon).isEmpty();

        verify(cacheService).get(TOP_HEAVIEST_CACHE_KEY);
        verify(fetchPokemonListUseCase).fetchPokemonList(List.of());
        verify(cacheService).set(anyString(), any());
    }
}