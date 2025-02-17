package com.jcondotta.pokemon.cache;

public enum TopRankingPokemonCacheKey {
    HEAVIEST("pokemon:ranking:heaviest:%s"),
    MOST_EXPERIENCED("pokemon:ranking:most-experienced:%s"),
    TALLEST("pokemon:ranking:tallest:%s");

    private final String keyTemplate;

    TopRankingPokemonCacheKey(String keyTemplate) {
        this.keyTemplate = keyTemplate;
    }

    public String format(int topN) {
        return String.format(this.keyTemplate, topN);
    }
}