package com.jcondotta.pokemon.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.jcondotta.pokemon.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CaffeineCacheService<K, V> implements CacheService<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaffeineCacheService.class);

    private final Cache<K, V> cache;

    public CaffeineCacheService(Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public void set(K cacheKey, V cacheValue) {
        Objects.requireNonNull(cacheKey, "cache.key.notNull");
        Objects.requireNonNull(cacheValue, "cache.value.notNull");

        LOGGER.debug("Adding entry to Caffeine cache: Key='{}', Value={}", cacheKey, cacheValue);

        cache.put(cacheKey, cacheValue);

        LOGGER.info("Cache store: Key='{}' successfully stored in cache.", cacheKey);
    }

    @Override
    public Optional<V> getOrFetch(K cacheKey) {
        Objects.requireNonNull(cacheKey, "cache.key.notNull");
        var cachedValue = cache.getIfPresent(cacheKey);

        if (Objects.nonNull(cachedValue)) {
            LOGGER.info("Cache hit: Key='{}' found.", cacheKey);
        }
        else {
            LOGGER.info("Cache miss: Key='{}' not found.", cacheKey);
        }

        return Optional.ofNullable(cachedValue);
    }

    @Override
    public V getOrFetch(K cacheKey, Function<K, V> valueLoader) {
        Objects.requireNonNull(cacheKey, "cache.key.notNull");
        Objects.requireNonNull(valueLoader, "cache.valueLoader.function.notNull");

        return cache.get(cacheKey, key -> {
            LOGGER.warn("Cache miss: Key='{}' not found, fetching from external source.", cacheKey);

            V loadedValue = valueLoader.apply(key);
            LOGGER.info("Cache store: Key='{}' successfully stored in cache.", cacheKey);
            return loadedValue;
        });
    }
}