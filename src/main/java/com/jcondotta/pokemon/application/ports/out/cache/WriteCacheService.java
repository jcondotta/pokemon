package com.jcondotta.pokemon.application.ports.out.cache;

public interface WriteCacheService<K, V> {

    void set(K cacheKey, V cacheValue);
}
