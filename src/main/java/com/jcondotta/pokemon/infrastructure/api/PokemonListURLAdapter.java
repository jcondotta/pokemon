package com.jcondotta.pokemon.infrastructure.api;

import com.jcondotta.pokemon.domain.ports.out.PokemonListURLPort;
import com.jcondotta.pokemon.service.client.exceptions.PokemonExternalAPIException;
import com.jcondotta.pokemon.service.client.list_urls.PokemonListURLResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;

@Service
public class PokemonListURLAdapter implements PokemonListURLPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonListURLAdapter.class);

    private final RestClient restClient;

    public PokemonListURLAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    public PokemonListURLResponse fetchPokemonURLs(URI uri) {
        var startTime = Instant.now();

        try {
            var pokemonBatchResponse = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new PokemonExternalAPIException(response.getStatusText(), response.getStatusCode());
                    })
                    .body(PokemonListURLResponse.class);

            long elapsedTimeMs = calculateElapsedTime(startTime);
            LOGGER.info("Successfully fetched Pokémon batch from {} in {} ms", uri.toURL(), elapsedTimeMs);

            return pokemonBatchResponse;
        }
        catch (PokemonExternalAPIException e) {
            long elapsedTimeMs = calculateElapsedTime(startTime);
            LOGGER.warn("API request failed with status {} - {} in {} ms", e.getStatusCode(), e.getMessage(), elapsedTimeMs);
        }
        catch (Exception e) {
            long elapsedTimeMs = calculateElapsedTime(startTime);
            LOGGER.error("Unexpected fetch failure in {} ms: {}", elapsedTimeMs, e.getMessage());
        }

        return null;
    }

    private long calculateElapsedTime(Instant startTime) {
        return Duration.between(startTime, Instant.now()).toMillis();
    }
}
