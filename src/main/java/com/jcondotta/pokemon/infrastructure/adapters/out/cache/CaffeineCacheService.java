package com.jcondotta.pokemon.infrastructure.adapters.out.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.jcondotta.pokemon.application.ports.out.cache.CacheService;
import jakarta.validation.constraints.NotNull;
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

        LOGGER.debug("Caffeine cache adding entry:  Key='{}', Value={}", cacheKey, cacheValue);

        cache.put(cacheKey, cacheValue);

        LOGGER.info("Cache store: Key='{}' successfully stored in cache.", cacheKey);
    }

    @Override
    public Optional<V> get(K cacheKey) {
        Objects.requireNonNull(cacheKey, "cache.key.notNull");
        var cachedValue = cache.getIfPresent(cacheKey);

        if (Objects.nonNull(cachedValue)) {
            LOGGER.info("Cache hit: Key='{}' -> Value={}", cacheKey, cachedValue);
        }
        else {
            LOGGER.info("Cache miss: Key='{}' not found.", cacheKey);
        }

        return Optional.ofNullable(cachedValue);
    }

    @Override
    public Optional<V> getOrFetch(@NotNull K cacheKey, Function<K, Optional<V>> valueLoader) {
        Objects.requireNonNull(cacheKey, "cache.key.notNull");
        Objects.requireNonNull(valueLoader, "cache.valueLoader.function.notNull");

        return Optional.ofNullable(cache.get(cacheKey, k -> {
            LOGGER.warn("Cache miss: Key='{}' not found, fetching from external source.", k);
            Optional<V> value = valueLoader.apply(k);

            if (value.isEmpty()) {
                LOGGER.warn("valueLoader returned empty for Key='{}'", k);
            }
            else {
                LOGGER.info("Value loaded and cached: Key='{}'", k);
            }
            return value.orElse(null);

        })).map(value -> {
            LOGGER.debug("Cache hit: Key='{}' -> Value={}", cacheKey, value);
            return value;
        });
    }
}