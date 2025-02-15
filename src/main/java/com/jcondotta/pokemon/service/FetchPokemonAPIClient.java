package com.jcondotta.pokemon.service;

import com.jcondotta.pokemon.model.Pokemon;
import com.jcondotta.pokemon.service.client.exceptions.PokemonExternalAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class FetchPokemonAPIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchPokemonAPIClient.class);

    private final RestClient restClient;
    private final String fetchPokemonURL;

    public FetchPokemonAPIClient(RestClient restClient, @Value("${pokemon.api.fetch-by-id.url}") String fetchPokemonURL) {
        this.restClient = restClient;
        this.fetchPokemonURL = fetchPokemonURL;
    }

    public Pokemon fetchPokemonById(int pokemonId) {
        URI pokemonURI = buildFetchPokemonURI(pokemonId);

        return restClient.get()
                .uri(pokemonURI)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {

                    var errorMessage = String.format(
                            "Pokémon API request failed with status %s while fetching Pokémon id: %d",
                            response.getStatusCode(), pokemonId
                    );

                    LOGGER.warn(errorMessage);
                    throw new PokemonExternalAPIException(errorMessage, response.getStatusCode());

                })
                .body(Pokemon.class);
    }

    private URI buildFetchPokemonURI(int pokemonId) {
        return UriComponentsBuilder.fromUriString(fetchPokemonURL)
                .buildAndExpand(pokemonId)
                .toUri();
    }
}
