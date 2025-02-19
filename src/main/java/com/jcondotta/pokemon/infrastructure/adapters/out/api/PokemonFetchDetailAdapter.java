package com.jcondotta.pokemon.infrastructure.adapters.out.api;

import com.jcondotta.pokemon.application.ports.out.api.PokemonFetchDetailsPort;
import com.jcondotta.pokemon.domain.model.Pokemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PokemonFetchDetailAdapter implements PokemonFetchDetailsPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonFetchDetailAdapter.class);

    private final RestClient restClient;
    private final String fetchPokemonURL;

    public PokemonFetchDetailAdapter(RestClient restClient, @Value("${pokemon.api.fetch-by-id.url}") String fetchPokemonURL) {
        this.restClient = restClient;
        this.fetchPokemonURL = fetchPokemonURL;
    }

    public Optional<Pokemon> fetchById(int pokemonId) {
        URI pokemonURI = buildFetchPokemonURI(pokemonId);
        var startTime = Instant.now();

        LOGGER.debug("Pokémon ID: {} - Fetching from external API...", pokemonId);

        try {
            var pokemon = restClient.get()
                    .uri(pokemonURI)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) ->
                            LOGGER.warn("Pokémon ID: {} - API request failed with status {}: {}",
                                    pokemonId, response.getStatusCode(), response.getStatusText()))
                    .body(Pokemon.class);

            var elapsedTimeMs = calculateElapsedTime(startTime);
            LOGGER.info("Pokémon ID: {} - Successfully fetched from API in {} ms", pokemonId, elapsedTimeMs);

            return Optional.ofNullable(pokemon);
        }
        catch (Exception e) {
            long elapsedTimeMs = calculateElapsedTime(startTime);
            LOGGER.error("Pokémon ID: {} - Fetch failed in {} ms: {}", pokemonId, elapsedTimeMs, e.getMessage());
        }

        return Optional.empty();
    }

    private URI buildFetchPokemonURI(int pokemonId) {
        return UriComponentsBuilder.fromUriString(fetchPokemonURL)
                .buildAndExpand(pokemonId)
                .toUri();
    }

    private long calculateElapsedTime(Instant startTime) {
        return Duration.between(startTime, Instant.now()).toMillis();
    }
}
