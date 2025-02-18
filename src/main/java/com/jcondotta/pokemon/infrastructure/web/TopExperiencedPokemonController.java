package com.jcondotta.pokemon.infrastructure.web;

import com.jcondotta.pokemon.service.dto.PokemonRankingListDTO;
import com.jcondotta.pokemon.service.ranking.TopExperiencedPokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PokemonAPIPath.POKEMON_TOP_EXPERIENCED)
public class TopExperiencedPokemonController {

    private final TopExperiencedPokemonService topExperiencedPokemonService;

    public TopExperiencedPokemonController(TopExperiencedPokemonService topExperiencedPokemonService) {
        this.topExperiencedPokemonService = topExperiencedPokemonService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Retrieve Top Experienced Pokémon",
            description = "Fetches a ranked list of the most experienced Pokémon available in the system. " +
                    "By default, it retrieves the top 5 experienced Pokémon."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the top experienced Pokémon",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PokemonRankingListDTO.class))
            ),
            @ApiResponse(responseCode = "400",
                    description = "Invalid topN parameter (must be between 1 and 20)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(responseCode = "500",
                    description = "Unexpected error while processing the request.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<PokemonRankingListDTO> findPokemon(
            @RequestParam(defaultValue = "5")
            @Parameter(description = "Number of top experienced Pokémon to retrieve (must be between 1 and 20)", example = "10")
            int topN) {

        var pokemonRankingDTOs = topExperiencedPokemonService.getTopExperiencedPokemon(topN);
        var pokemonRankingListDTO = new PokemonRankingListDTO(pokemonRankingDTOs);

        return ResponseEntity.ok(pokemonRankingListDTO);
    }
}
