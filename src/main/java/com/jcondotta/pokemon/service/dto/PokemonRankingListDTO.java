package com.jcondotta.pokemon.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PokemonRankingListDTO", description = "Represents a ranked list of Pokémon based on specific criteria, such as weight, height, or experience.")
public class PokemonRankingListDTO {

    @Schema(description = "A list containing Pokémon ranked by the specified criterion.",
            example = "[{\"name\": \"Snorlax\", \"rankingValue\": 4600.0}, {\"name\": \"Groudon\", \"rankingValue\": 950.0}]")
    private final List<PokemonRankingDTO> pokemonList;

    @Schema(description = "The total number of Pokémon included in the ranking.", example = "2")
    private final int count;

    public PokemonRankingListDTO(List<PokemonRankingDTO> pokemonList) {
        this.pokemonList = pokemonList;
        this.count = pokemonList.size();
    }

    public int getCount() {
        return count;
    }

    public List<PokemonRankingDTO> getPokemonList() {
        return pokemonList;
    }
}
