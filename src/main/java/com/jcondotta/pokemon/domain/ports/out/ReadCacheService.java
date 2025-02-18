package com.jcondotta.pokemon.domain.ports.out;

import java.util.Optional;
import java.util.function.Function;

public interface ReadCacheService<K, V> {

    Optional<V> get(K cacheKey);
    Optional<V> getOrFetch(K cacheKey, Function<K, Optional<V>> valueLoader);
}