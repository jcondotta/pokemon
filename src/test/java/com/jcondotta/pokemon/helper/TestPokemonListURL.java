package com.jcondotta.pokemon.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.pokemon.domain.model.Pokemon;

import java.util.*;
import java.util.stream.Collectors;

public class TestPokemonListURL {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String BASE_URL = "https://pokeapi.co/api/v2/pokemon";

    private static final List<Pokemon> sortedPokemonList = Arrays.stream(TestPokemon.values())
            .sorted(Comparator.comparingInt(TestPokemon::getId))
            .map(TestPokemon::pokemonDetailsToPokemon)
            .toList();

    public static String generatePaginatedResponse(int limit, int offset) {
        int totalCount = sortedPokemonList.size();

        if (offset >= totalCount) {
            return "{\"count\": " + totalCount + ", \"next\": null, \"results\": []}";
        }

        List<Pokemon> paginatedList = sortedPokemonList.stream()
                .skip(offset)
                .limit(limit)
                .toList();

        String nextUrl = (offset + limit) < totalCount
                ? BASE_URL + "?offset=" + (offset + limit) + "&limit=" + limit : null;

        try {
            Map<String, Object> batchResponse = new HashMap<>();
            batchResponse.put("count", totalCount);
            batchResponse.put("results", paginatedList.stream()
                    .map(pokemon -> Map.of(
                            "name", pokemon.name(),
                            "url", BASE_URL + "/" + pokemon.id() + "/"
                    ))
                    .collect(Collectors.toList()));

            if (nextUrl != null) {
                batchResponse.put("next", nextUrl);
            }

            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(batchResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to generate Pokémon batch response", e);
        }
    }
}
