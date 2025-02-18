package com.jcondotta.pokemon.domain.ports.out;

public interface CacheService<K, V> extends ReadCacheService<K, V>, WriteCacheService<K, V> {
}