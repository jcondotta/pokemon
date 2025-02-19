package com.jcondotta.pokemon.application.ports.out.cache;

public interface CacheService<K, V> extends ReadCacheService<K, V>, WriteCacheService<K, V> {
}