package com.jcondotta.pokemon.service;

public enum PokemonCacheKey {
    DETAILS("pokemon:details:%s");

    private final String keyTemplate;

    PokemonCacheKey(String keyTemplate) {
        this.keyTemplate = keyTemplate;
    }

    public String format(int pokemonId) {
        return String.format(this.keyTemplate, pokemonId);
    }
}