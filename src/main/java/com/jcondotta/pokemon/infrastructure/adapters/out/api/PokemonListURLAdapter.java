package com.jcondotta.pokemon.infrastructure.adapters.out.api;

import com.jcondotta.pokemon.application.ports.out.api.PokemonListURLPort;
import com.jcondotta.pokemon.domain.model.PokemonListURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PokemonListURLAdapter implements PokemonListURLPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonListURLAdapter.class);

    private final RestClient restClient;

    public PokemonListURLAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<PokemonListURL> fetchPokemonURLs(URI uri) {
        var startTime = Instant.now();

        try {
            var pokemonBatchResponse = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) ->
                            LOGGER.warn("API request failed with status {}: {}", response.getStatusCode(), response.getStatusText())
                    )
                    .body(PokemonListURL.class);

            long elapsedTimeMs = calculateElapsedTime(startTime);
            LOGGER.info("Successfully fetched Pokémon batch from {} in {} ms", uri.toURL(), elapsedTimeMs);

            return Optional.ofNullable(pokemonBatchResponse);
        }
        catch (Exception e) {
            long elapsedTimeMs = calculateElapsedTime(startTime);
            LOGGER.error("Unexpected fetch failure in {} ms: {}", elapsedTimeMs, e.getMessage());
        }

        return Optional.empty();
    }

    private long calculateElapsedTime(Instant startTime) {
        return Duration.between(startTime, Instant.now()).toMillis();
    }
}
