package com.jcondotta.pokemon.application.service;

import com.jcondotta.pokemon.application.ports.out.api.PokemonListURLPort;
import com.jcondotta.pokemon.application.usecases.FetchAllPokemonIdsUseCase;
import com.jcondotta.pokemon.domain.model.PokemonListURL;
import com.jcondotta.pokemon.infrastructure.config.PokemonListURLProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FetchAllPokemonIdsService implements FetchAllPokemonIdsUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchAllPokemonIdsService.class);

    private final PokemonListURLPort pokemonListURLPort;
    private final PokemonListURLProperties pokemonListURLProperties;

    public FetchAllPokemonIdsService(PokemonListURLPort pokemonListURLPort,
                                     PokemonListURLProperties pokemonListURLProperties) {
        this.pokemonListURLPort = pokemonListURLPort;
        this.pokemonListURLProperties = pokemonListURLProperties;
    }

    public List<Integer> fetchAllPokemonIds() {
        LOGGER.info("Starting All Pokémon ID fetch process.");
        List<Integer> allPokemonIds = new ArrayList<>();

        String nextURL = pokemonListURLProperties.url();
        int page = 1;

        while (nextURL != null && !nextURL.isEmpty()) {
            LOGGER.info("Fetching page {} from URL: {}", page, nextURL);

            Optional<PokemonListURL> responseOpt = fetchPokemonIdsFromAPI(nextURL);

            if (responseOpt.isPresent()) {
                PokemonListURL response = responseOpt.get();

                response.results().forEach(pokemon -> allPokemonIds.add(extractPokemonIdFromURL(pokemon.url())));

                LOGGER.debug("Fetched {} Pokémon from page {} (Total collected: {})",
                        response.results().size(), page, allPokemonIds.size());

                nextURL = Optional.ofNullable(response.next()).orElse("");
                page++;
            } else {
                LOGGER.warn("Failed to fetch data for page {}. Stopping pagination.", page);
                break;
            }
        }

        LOGGER.info("Completed fetching {} Pokémon IDs.", allPokemonIds.size());
        return allPokemonIds;
    }


    private Optional<PokemonListURL> fetchPokemonIdsFromAPI(String url) {
        if (url == null || url.isEmpty()) {
            LOGGER.warn("Attempted to fetch from an empty or null URL. Skipping request.");
            return Optional.empty();
        }

        try {
            Instant start = Instant.now();
            Optional<PokemonListURL> response = pokemonListURLPort.fetchPokemonURLs(URI.create(url));
            long duration = Duration.between(start, Instant.now()).toMillis();

            LOGGER.debug("API call to {} completed in {} ms.", url, duration);
            return response;
        }
        catch (Exception e) {
            LOGGER.error("API request failed for URL {} - Error: {}", url, e.getMessage());
            return Optional.empty();
        }
    }

    private int extractPokemonIdFromURL(String url) {
        try {
            String[] parts = url.split("/");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to extract Pokémon ID from URL: {} - Error: {}", url, e.getMessage(), e);
            throw e;
        }
    }
}
