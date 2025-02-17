package com.jcondotta.pokemon.cache;

public enum PokemonIdsCacheKey {
    ALL_IDS("pokemon:allIds");

    private final String keyTemplate;

    PokemonIdsCacheKey(String keyTemplate) {
        this.keyTemplate = keyTemplate;
    }

    public String getKeyTemplate() {
        return keyTemplate;
    }
}