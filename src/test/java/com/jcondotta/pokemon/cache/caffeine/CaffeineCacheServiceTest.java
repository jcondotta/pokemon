package com.jcondotta.pokemon.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.jcondotta.pokemon.TestPokemon;
import com.jcondotta.pokemon.cache.CacheService;
import com.jcondotta.pokemon.service.PokemonCacheKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaffeineCacheServiceTest {

    private static final Integer CHARIZARD_ID = TestPokemon.CHARIZARD.getId();
    private static final String CHARIZARD_CACHE_KEY = PokemonCacheKey.DETAILS.format(CHARIZARD_ID);
    private static final String CHARIZARD_CACHE_VALUE = TestPokemon.CHARIZARD.getDetails();

    @Mock
    private Cache<String, String> mockCache;

    @Mock
    private Function<String, String> valueLoaderMock;

    private CacheService<String, String> cacheService;

    @BeforeEach
    public void beforeEach(){
        cacheService = new CaffeineCacheService<>(mockCache);
    }

    @Test
    void shouldStoreEntryInCache_whenCacheEntryIsValid() {
        cacheService.set(CHARIZARD_CACHE_KEY, CHARIZARD_CACHE_VALUE);

        verify(mockCache).put(eq(CHARIZARD_CACHE_KEY), eq(CHARIZARD_CACHE_VALUE));
    }

    @Test
    void shouldThrowNullPointerException_whenSetEntryCacheKeyIsNull() {
        assertThatThrownBy(() -> cacheService.set(null, CHARIZARD_CACHE_VALUE))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("cache.key.notNull");

        verify(mockCache, never()).put(anyString(), anyString());
    }

    @Test
    void shouldThrowNullPointerException_whenSetEntryCacheValueIsNull() {
        assertThatThrownBy(() -> cacheService.set(CHARIZARD_CACHE_KEY, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("cache.value.notNull");

        verify(mockCache, never()).put(anyString(), anyString());
    }

    @Test
    void shouldReturnCachedValue_whenCacheKeyExists() {
        when(mockCache.getIfPresent(CHARIZARD_CACHE_KEY))
                .thenReturn(CHARIZARD_CACHE_VALUE);

        var optCacheValue = cacheService.getOrFetch(CHARIZARD_CACHE_KEY);
        assertThat(optCacheValue)
                .isPresent()
                .hasValue(CHARIZARD_CACHE_VALUE);

        verify(mockCache).getIfPresent(CHARIZARD_CACHE_KEY);
    }

    @Test
    void shouldReturnEmptyOption_whenMissingCacheKey() {
        when(mockCache.getIfPresent(CHARIZARD_CACHE_KEY))
                .thenReturn(null);

        var optCacheValue = cacheService.getOrFetch(CHARIZARD_CACHE_KEY);
        assertThat(optCacheValue).isEmpty();

        verify(mockCache).getIfPresent(CHARIZARD_CACHE_KEY);
    }

    @Test
    void shouldThrowNullPointerException_whenValueLoaderIsNullInGetOrFetch() {
        assertThatThrownBy(() -> cacheService.getOrFetch(CHARIZARD_CACHE_KEY, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("cache.valueLoader.function.notNull");

        verify(mockCache, never()).put(anyString(), anyString());
    }

    @Test
    void shouldReturnCachedValue_whenKeyExistsInGetOrFetch(){
        when(mockCache.get(eq(CHARIZARD_CACHE_KEY), ArgumentMatchers.<Function<String, String>>any()))
                .thenReturn(CHARIZARD_CACHE_VALUE);

        var cacheValue = cacheService.getOrFetch(CHARIZARD_CACHE_KEY, valueLoaderMock);
        assertThat(cacheValue).isEqualTo(CHARIZARD_CACHE_VALUE);

        verify(mockCache).get(eq(CHARIZARD_CACHE_KEY), any());
        verifyNoMoreInteractions(mockCache);
    }

    @Test
    void shouldFetchAndCachedValue_whenCacheMissInGetOrFetch() {
        when(mockCache.get(eq(CHARIZARD_CACHE_KEY), ArgumentMatchers.<Function<String, String>>any()))
                .thenAnswer(invocation -> {
                    Function<String, String> loader = invocation.getArgument(1);
                    return loader.apply(CHARIZARD_CACHE_KEY);
                });

        var cacheValue = cacheService.getOrFetch(CHARIZARD_CACHE_KEY, key -> CHARIZARD_CACHE_VALUE);
        assertThat(cacheValue).isEqualTo(CHARIZARD_CACHE_VALUE);

        verify(mockCache).get(eq(CHARIZARD_CACHE_KEY), any(Function.class));
    }

    @Test
    void shouldReturnCachedValueAndNotCallValueLoader_whenCachedValueExists() {
        when(mockCache.get(eq(CHARIZARD_CACHE_KEY), ArgumentMatchers.<Function<String, String>>any()))
                .thenReturn(CHARIZARD_CACHE_VALUE);

        var cacheValue = cacheService.getOrFetch(CHARIZARD_CACHE_KEY, valueLoaderMock);
        assertThat(cacheValue).isEqualTo(CHARIZARD_CACHE_VALUE);

        verify(mockCache).get(eq(CHARIZARD_CACHE_KEY), any(Function.class));
        verify(valueLoaderMock, never()).apply(anyString());
    }
}
