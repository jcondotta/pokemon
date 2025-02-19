package com.jcondotta.pokemon.infrastructure.adapters.in.api;

public interface PokemonAPIPath {

    String POKEMON_BASE_PATH_API_V1_MAPPING = "/api/v1/pokemon";
    String POKEMON_TOP_HEAVIEST = POKEMON_BASE_PATH_API_V1_MAPPING + "/top-heaviest";
    String POKEMON_TOP_TALLEST = POKEMON_BASE_PATH_API_V1_MAPPING + "/top-tallest";
    String POKEMON_TOP_EXPERIENCED = POKEMON_BASE_PATH_API_V1_MAPPING + "/top-experienced";
}
