package com.jcondotta.pokemon.infrastructure.persistence.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jcondotta.pokemon.domain.model.Pokemon;
import com.jcondotta.pokemon.helper.TestPokemon;
import com.jcondotta.pokemon.domain.ports.out.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaffeineCacheServiceTest {

    private static final Integer CHARIZARD_ID = TestPokemon.CHARIZARD.getId();
    private static final String CHARIZARD_CACHE_KEY = PokemonCacheKey.POKEMON_DETAILS.format(CHARIZARD_ID);
    private static final String CHARIZARD_CACHE_VALUE = TestPokemon.CHARIZARD.getDetails();

    @Mock
    private Cache<String, String> mockCache;

    @Mock
    private Function<String, Optional<String>> valueLoaderMock;

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

        var optCacheValue = cacheService.get(CHARIZARD_CACHE_KEY);
        assertThat(optCacheValue)
                .isPresent()
                .hasValue(CHARIZARD_CACHE_VALUE);

        verify(mockCache).getIfPresent(CHARIZARD_CACHE_KEY);
    }

    @Test
    void shouldReturnEmptyOption_whenMissingCacheKey() {
        when(mockCache.getIfPresent(CHARIZARD_CACHE_KEY))
                .thenReturn(null);

        var optCacheValue = cacheService.get(CHARIZARD_CACHE_KEY);
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
        when(mockCache.get(eq(CHARIZARD_CACHE_KEY), any())).thenReturn(CHARIZARD_CACHE_VALUE);

        var cacheValue = cacheService.getOrFetch(CHARIZARD_CACHE_KEY, valueLoaderMock);
        assertThat(cacheValue).hasValue(CHARIZARD_CACHE_VALUE);

        verify(mockCache).get(eq(CHARIZARD_CACHE_KEY), any());
        verifyNoMoreInteractions(mockCache);
    }

    @Test
    @Disabled
    void shouldFetchAndCacheValue_whenCacheMissInGetOrFetch() {
        when(mockCache.get(eq(CHARIZARD_CACHE_KEY), any()))
                .thenAnswer(invocation -> {
                    Function<String, String> loader = invocation.getArgument(1);
                    return loader.apply(CHARIZARD_CACHE_KEY);
                });

        var cacheValue = cacheService.getOrFetch(CHARIZARD_CACHE_KEY,
                key -> Optional.of(CHARIZARD_CACHE_VALUE));
        assertThat(cacheValue).hasValue(CHARIZARD_CACHE_VALUE);

        verify(mockCache).get(eq(CHARIZARD_CACHE_KEY), any());
    }

    @Test
    @Disabled
    void shouldReturnCachedValueAndNotCallValueLoader_whenCachedValueExists() {
        when(mockCache.get(eq(CHARIZARD_CACHE_KEY), any())).thenReturn(CHARIZARD_CACHE_VALUE);

        var cacheValue = cacheService.getOrFetch(CHARIZARD_CACHE_KEY, valueLoaderMock);
        assertThat(cacheValue).hasValue(CHARIZARD_CACHE_VALUE);

        verify(mockCache).get(eq(CHARIZARD_CACHE_KEY), any());
        verify(valueLoaderMock, never()).apply(anyString());
    }

    @Test
    void shouldReturnSameCachedValue_whenMultipleThreadsAccessCacheConcurrently() throws InterruptedException, ExecutionException {
        Cache<String, String> caffeineCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();

        cacheService = new CaffeineCacheService<>(caffeineCache);

        when(valueLoaderMock.apply(eq(CHARIZARD_CACHE_KEY)))
                .thenAnswer(invocation -> Optional.of(CHARIZARD_CACHE_VALUE));

        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Optional<String>>> futures;
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            futures = new ArrayList<>();

            // Submit tasks so that all threads wait on the latch, then access the cache concurrently.
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    latch.await(); // Ensure all threads start at the same time.
                    Optional<String> result = cacheService.getOrFetch(CHARIZARD_CACHE_KEY, valueLoaderMock);
                    System.out.printf("Thread %d got: %s%n", Thread.currentThread().getId(), result);
                    return result;
                }));
            }

            // Release the latch.
            latch.countDown();
//            executor.shutdown();
//            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        // Verify that all threads got the expected cached value.
        for (Future<Optional<String>> future : futures) {
            assertThat(future.get()).as("All threads should get the same cached value")
                    .hasValue(CHARIZARD_CACHE_VALUE);
        }

        // Verify that the loader was invoked exactly once.
        verify(valueLoaderMock, times(1)).apply(eq(CHARIZARD_CACHE_KEY));
    }

}
