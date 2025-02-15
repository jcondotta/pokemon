package com.jcondotta.pokemon.cache;

public interface CacheService<K, V> extends ReadCacheService<K, V>, WriteCacheService<K, V> {
}