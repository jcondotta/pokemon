package com.jcondotta.pokemon.application.service;

import com.jcondotta.pokemon.application.ports.out.api.PokemonFetchDetailsPort;
import com.jcondotta.pokemon.application.ports.out.cache.CacheService;
import com.jcondotta.pokemon.domain.model.Pokemon;
import com.jcondotta.pokemon.helper.TestPokemon;
import com.jcondotta.pokemon.infrastructure.adapters.out.cache.PokemonCacheKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonDetailsFetcherServiceTest {

    private static final Integer KADABRA_ID = TestPokemon.KADABRA.getId();
    private static final Pokemon KADABRA = TestPokemon.KADABRA.pokemonDetailsToPokemon();

    private static final String KADABRA_CACHE_KEY = PokemonCacheKeys.pokemonDetails(KADABRA_ID);

    @Mock
    private CacheService<String, Pokemon> cacheService;

    @Mock
    private PokemonFetchDetailsPort pokemonFetchDetailsPort;

    @InjectMocks
    private PokemonDetailsFetcherService pokemonDetailsFetcherService;

    @Test
    void shouldReturnCachedPokemon_whenCacheEntryExists() {
        when(cacheService.getOrFetch(eq(KADABRA_CACHE_KEY), any()))
                .thenReturn(Optional.of(KADABRA));

        assertThat(pokemonDetailsFetcherService.fetchById(KADABRA_ID))
                .hasValue(KADABRA);

        verify(cacheService).getOrFetch(anyString(), any());
        verify(cacheService, never()).set(anyString(), any());
        verify(pokemonFetchDetailsPort, never()).fetchById(anyInt());
    }

    @Test
    void shouldFetchFromAPIAndSetResultInCache_whenCacheMissHappens() {
        when(cacheService.getOrFetch(eq(KADABRA_CACHE_KEY), any()))
                .thenAnswer(invocation -> {
                    Function<String, Pokemon> loader = invocation.getArgument(1);
                    return loader.apply(KADABRA_CACHE_KEY);
                });

        when(pokemonFetchDetailsPort.fetchById(KADABRA_ID)).thenReturn(Optional.of(KADABRA));

        assertThat(pokemonDetailsFetcherService.fetchById(KADABRA_ID))
                .hasValue(KADABRA);

        verify(pokemonFetchDetailsPort).fetchById(KADABRA_ID);
    }

    @Test
    void shouldReturnEmptyOptional_whenPokemonAPIFails() {
        when(cacheService.getOrFetch(eq(KADABRA_CACHE_KEY), any()))
                .thenAnswer(invocation -> {
                    Function<String, Optional<Pokemon>> loader = invocation.getArgument(1);
                    return loader.apply(KADABRA_CACHE_KEY);
                });

        when(pokemonFetchDetailsPort.fetchById(KADABRA_ID)).thenReturn(Optional.empty());

        assertThat(pokemonDetailsFetcherService.fetchById(KADABRA_ID)).isEmpty();

        verify(pokemonFetchDetailsPort).fetchById(KADABRA_ID);
        verify(cacheService, never()).set(anyString(), any());
    }
}
