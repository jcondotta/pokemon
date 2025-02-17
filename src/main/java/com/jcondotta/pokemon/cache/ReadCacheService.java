package com.jcondotta.pokemon.cache;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.function.Function;

public interface ReadCacheService<K, V> {

    Optional<V> get(@NotNull K cacheKey);
    Optional<V> getOrFetch(@NotNull K cacheKey, Function<K, Optional<V>> valueLoader);
}