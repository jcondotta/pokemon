package com.jcondotta.pokemon.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.jcondotta.pokemon.TestPokemon;
import com.jcondotta.pokemon.cache.CacheService;
import com.jcondotta.pokemon.service.PokemonCacheKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaffeineCacheServiceTest {

    private static final String CHARIZARD_CACHE_KEY = PokemonCacheKey.DETAILS
            .format(TestPokemon.CHARIZARD.getId());

    private static final String CHARIZARD_CACHE_VALUE = TestPokemon.CHARIZARD.getDetails();

    @Mock
    private Cache<String, String> mockCache;

    private CacheService<String, String> cacheService;

    @BeforeEach
    public void beforeEach(){
        cacheService = new CaffeineCacheService<>(mockCache);
    }

    @Test
    void shouldStoreEntryInCache_whenCacheEntryIsValid() {
        cacheService.setCacheEntry(CHARIZARD_CACHE_KEY, CHARIZARD_CACHE_VALUE);

        verify(mockCache).put(eq(CHARIZARD_CACHE_KEY), eq(CHARIZARD_CACHE_VALUE));
    }

    @Test
    void shouldThrowNullPointerException_whenSetEntryCacheKeyIsNull() {
        assertThatThrownBy(() -> cacheService.setCacheEntry(null, CHARIZARD_CACHE_VALUE))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("cache.key.notNull");

        verify(mockCache, never()).put(anyString(), anyString());
    }

    @Test
    void shouldThrowNullPointerException_whenSetEntryCacheValueIsNull() {
        assertThatThrownBy(() -> cacheService.setCacheEntry(CHARIZARD_CACHE_KEY, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("cache.value.notNull");

        verify(mockCache, never()).put(anyString(), anyString());
    }

    @Test
    void shouldReturnCachedValue_whenCacheKeyExists() {
        when(mockCache.getIfPresent(CHARIZARD_CACHE_KEY))
                .thenReturn(CHARIZARD_CACHE_VALUE);

        var optCacheValue = cacheService.getCacheEntryValue(CHARIZARD_CACHE_KEY);
        assertThat(optCacheValue)
                .isPresent()
                .hasValue(CHARIZARD_CACHE_VALUE);

        verify(mockCache).getIfPresent(CHARIZARD_CACHE_KEY);
    }

    @Test
    void shouldReturnEmptyOption_whenMissingCacheKey() {
        when(mockCache.getIfPresent(CHARIZARD_CACHE_KEY))
                .thenReturn(null);

        var optCacheValue = cacheService.getCacheEntryValue(CHARIZARD_CACHE_KEY);
        assertThat(optCacheValue).isEmpty();

        verify(mockCache).getIfPresent(CHARIZARD_CACHE_KEY);
    }
}
