package com.jcondotta.pokemon.infrastructure.adapters.out.cache;

public class PokemonCacheKeys {

    private static final String POKEMON_DETAILS_TEMPLATE = "pokemon:details:%s";
    private static final String POKEMON_IDS_TEMPLATE = "pokemon:allIds";
    private static final String TOP_RANKING_TEMPLATE = "pokemon:ranking:%s:%s";

    private PokemonCacheKeys() {}

    public static String pokemonDetails(int pokemonId) {
        return String.format(POKEMON_DETAILS_TEMPLATE, pokemonId);
    }

    public static String allPokemonIds() {
        return POKEMON_IDS_TEMPLATE;
    }

    public static String topRanking(PokemonRankingType rankingType, int topN) {
        return String.format(TOP_RANKING_TEMPLATE, rankingType.getValue(), topN);
    }
}
