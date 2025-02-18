package com.jcondotta.pokemon.infrastructure.web;

import com.jcondotta.pokemon.service.dto.PokemonRankingListDTO;
import com.jcondotta.pokemon.service.ranking.TopHeaviestPokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PokemonAPIPath.POKEMON_TOP_HEAVIEST)
public class TopHeaviestPokemonController {

    private final TopHeaviestPokemonService topHeaviestPokemonService;

    public TopHeaviestPokemonController(TopHeaviestPokemonService topHeaviestPokemonService) {
        this.topHeaviestPokemonService = topHeaviestPokemonService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Retrieve Top Heaviest Pokémon",
            description = "Fetches a ranked list of the heaviest Pokémon available in the system. " +
                    "By default, it retrieves the top 5 heaviest Pokémon."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the top heaviest Pokémon",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PokemonRankingListDTO.class))
            ),
            @ApiResponse(responseCode = "400",
                    description = "Invalid topN parameter (must be positive)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(responseCode = "500",
                    description = "Unexpected error while processing the request.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<PokemonRankingListDTO> findPokemon(
            @RequestParam(defaultValue = "5")
            @Parameter(description = "Number of top heaviest Pokémon to retrieve (must be positive)", example = "10")
            int topN) {

        var pokemonRankingDTOs = topHeaviestPokemonService.getTopHeaviestPokemon(topN);
        var pokemonRankingListDTO = new PokemonRankingListDTO(pokemonRankingDTOs);

        return ResponseEntity.ok(pokemonRankingListDTO);
    }
}
