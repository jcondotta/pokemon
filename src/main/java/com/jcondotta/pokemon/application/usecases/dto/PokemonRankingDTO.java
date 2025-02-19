package com.jcondotta.pokemon.application.usecases.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PokemonRankingDTO", description = "Represents a ranked Pokémon based on a specific criterion, such as weight, height, or experience.")
public record PokemonRankingDTO(

        @Schema(description = "The name of the Pokémon.", example = "Snorlax")
        String name,

        @Schema(description = "The ranking value based on the chosen criterion (e.g., weight in kg, height in meters, or base experience points).", example = "4600.0")
        Object rankingValue
) {}