package com.jcondotta.pokemon.infrastructure.persistence.cache;

public enum PokemonCacheKey {
    POKEMON_DETAILS("pokemon:details:%s");

    private final String keyTemplate;

    PokemonCacheKey(String keyTemplate) {
        this.keyTemplate = keyTemplate;
    }

    public String format(Object... args) {
        return String.format(this.keyTemplate, args);
    }
}