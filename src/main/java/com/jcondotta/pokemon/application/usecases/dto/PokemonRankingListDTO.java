package com.jcondotta.pokemon.application.usecases.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;

@Schema(name = "PokemonRankingListDTO", description = "Represents a ranked list of Pokémon based on specific criteria, such as weight, height, or experience.")
public class PokemonRankingListDTO {

    @Schema(description = "A list containing Pokémon ranked by the specified criterion.",
            example = "[{\"name\": \"Snorlax\", \"rankingValue\": 4600.0}, {\"name\": \"Groudon\", \"rankingValue\": 950.0}]")
    private final Collection<PokemonRankingDTO> pokemonList;

    @Schema(description = "The total number of Pokémon included in the ranking.", example = "2")
    private final int count;

    public PokemonRankingListDTO(Collection<PokemonRankingDTO> pokemonList) {
        this.pokemonList = pokemonList;
        this.count = pokemonList.size();
    }

    public int getCount() {
        return count;
    }

    public Collection<PokemonRankingDTO> getPokemonList() {
        return pokemonList;
    }
}
