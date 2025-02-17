package com.jcondotta.pokemon.service;

import com.jcondotta.pokemon.cache.CacheService;
import com.jcondotta.pokemon.cache.PokemonCacheKey;
import com.jcondotta.pokemon.helper.TestPokemon;
import com.jcondotta.pokemon.model.Pokemon;
import com.jcondotta.pokemon.service.client.PokemonDetailRestClientAPI;
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
class PokemonDetailFetcherServiceTest {

    private static final Integer KADABRA_ID = TestPokemon.KADABRA.getId();
    private static final Pokemon KADABRA = TestPokemon.KADABRA.pokemonDetailsToPokemon();

    private static final String KADABRA_CACHE_KEY = PokemonCacheKey.POKEMON_DETAILS.format(KADABRA_ID);

    @Mock
    private CacheService<String, Pokemon> cacheService;

    @Mock
    private PokemonDetailRestClientAPI pokemonAPIClient;

    @InjectMocks
    private PokemonDetailFetcherService pokemonDetailFetcherService;

    @Test
    void shouldReturnCachedPokemon_whenCacheEntryExists() {
        when(cacheService.getOrFetch(eq(KADABRA_CACHE_KEY), any()))
                .thenReturn(Optional.of(KADABRA));

        assertThat(pokemonDetailFetcherService.fetchById(KADABRA_ID))
                .hasValue(KADABRA);

        verify(cacheService).getOrFetch(anyString(), any());
        verify(cacheService, never()).set(anyString(), any());
        verify(pokemonAPIClient, never()).fetchById(anyInt());
    }

    @Test
    void shouldFetchFromAPIAndSetResultInCache_whenCacheMissHappens() {
        when(cacheService.getOrFetch(eq(KADABRA_CACHE_KEY), any()))
                .thenAnswer(invocation -> {
                    Function<String, Pokemon> loader = invocation.getArgument(1);
                    return loader.apply(KADABRA_CACHE_KEY);
                });

        when(pokemonAPIClient.fetchById(KADABRA_ID)).thenReturn(Optional.of(KADABRA));

        assertThat(pokemonDetailFetcherService.fetchById(KADABRA_ID))
                .hasValue(KADABRA);

        verify(pokemonAPIClient).fetchById(KADABRA_ID);
    }

    @Test
    void shouldReturnEmptyOptional_whenPokemonAPIFails() {
        when(cacheService.getOrFetch(eq(KADABRA_CACHE_KEY), any()))
                .thenAnswer(invocation -> {
                    Function<String, Optional<Pokemon>> loader = invocation.getArgument(1);
                    return loader.apply(KADABRA_CACHE_KEY);
                });

        when(pokemonAPIClient.fetchById(KADABRA_ID)).thenReturn(Optional.empty());

        assertThat(pokemonDetailFetcherService.fetchById(KADABRA_ID)).isEmpty();

        verify(pokemonAPIClient).fetchById(KADABRA_ID);
        verify(cacheService, never()).set(anyString(), any());
    }
}
