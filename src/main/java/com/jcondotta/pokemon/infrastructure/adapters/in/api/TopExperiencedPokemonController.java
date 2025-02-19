package com.jcondotta.pokemon.infrastructure.adapters.in.api;

import com.jcondotta.pokemon.application.usecases.FetchTopRankedPokemonUseCase;
import com.jcondotta.pokemon.application.usecases.dto.PokemonRankingListDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PokemonAPIPath.POKEMON_TOP_EXPERIENCED)
public class TopExperiencedPokemonController {

    private final FetchTopRankedPokemonUseCase topRankedPokemonUseCase;

    public TopExperiencedPokemonController(@Qualifier("topExperiencedPokemonService") FetchTopRankedPokemonUseCase topRankedPokemonUseCase) {
        this.topRankedPokemonUseCase = topRankedPokemonUseCase;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Retrieve Top Experienced Pokémon",
            description = "Fetches a ranked list of the most experienced Pokémon available in the system. " +
                    "By default, it retrieves the top 5 most experienced Pokémon."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the top experienced Pokémon",
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
            @Parameter(description = "Number of top experienced Pokémon to retrieve (must be positive)", example = "10")
            int topN) {

        var pokemonRankingDTOs = topRankedPokemonUseCase.getTopRankedPokemon(topN);
        var pokemonRankingListDTO = new PokemonRankingListDTO(pokemonRankingDTOs);

        return ResponseEntity.ok(pokemonRankingListDTO);
    }
}
