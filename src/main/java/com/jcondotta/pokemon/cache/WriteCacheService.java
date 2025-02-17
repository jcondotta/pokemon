package com.jcondotta.pokemon.cache;

import jakarta.validation.constraints.NotNull;

public interface WriteCacheService<K, V> {

    void set(@NotNull K cacheKey, @NotNull V cacheValue);
}
