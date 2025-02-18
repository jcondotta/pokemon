package com.jcondotta.pokemon.domain.ports.out;

public interface WriteCacheService<K, V> {

    void set(K cacheKey, V cacheValue);
}
