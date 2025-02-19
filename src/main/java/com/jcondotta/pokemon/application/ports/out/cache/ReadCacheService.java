package com.jcondotta.pokemon.application.ports.out.cache;

import java.util.Optional;
import java.util.function.Function;

public interface ReadCacheService<K, V> {

    Optional<V> get(K cacheKey);
    Optional<V> getOrFetch(K cacheKey, Function<K, Optional<V>> valueLoader);
}