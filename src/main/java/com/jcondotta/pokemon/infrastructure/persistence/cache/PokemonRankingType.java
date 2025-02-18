package com.jcondotta.pokemon.infrastructure.persistence.cache;

public enum PokemonRankingType {
    HEAVIEST("heaviest"),
    TALLEST("tallest"),
    MOST_EXPERIENCED("most-experienced");

    private final String value;

    PokemonRankingType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
