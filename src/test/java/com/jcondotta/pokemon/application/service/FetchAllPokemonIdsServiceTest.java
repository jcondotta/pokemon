package com.jcondotta.pokemon.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.pokemon.application.ports.out.api.PokemonListURLPort;
import com.jcondotta.pokemon.application.usecases.FetchAllPokemonIdsUseCase;
import com.jcondotta.pokemon.domain.model.PokemonListURL;
import com.jcondotta.pokemon.domain.model.PokemonURL;
import com.jcondotta.pokemon.helper.TestPokemonListURL;
import com.jcondotta.pokemon.infrastructure.config.PokemonListURLProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchAllPokemonIdsServiceTest {

    private final PokemonListURLProperties pokemonListURLProperties =
            new PokemonListURLProperties("https://pokeapi.co/api/v2/pokemon", 2);

    @Mock
    private PokemonListURLPort pokemonListURLPort;

    private FetchAllPokemonIdsUseCase fetchAllPokemonIdsUseCase;

    @BeforeEach
    void setUp() {
        fetchAllPokemonIdsUseCase = new FetchAllPokemonIdsService(pokemonListURLPort, pokemonListURLProperties);
    }

    @Test
    void shouldFetchAllPokemonIds_whenPaginationIsSuccessful() {
        String firstPageJson = TestPokemonListURL.generatePaginatedResponse(10, 0);
        String secondPageJson = TestPokemonListURL.generatePaginatedResponse(10, 10);

        Optional<PokemonListURL> firstPage = Optional.ofNullable(parseJsonToPokemonListURL(firstPageJson));
        Optional<PokemonListURL> secondPage = Optional.ofNullable(parseJsonToPokemonListURL(secondPageJson));

        doReturn(firstPage)
                .doReturn(secondPage)
                .when(pokemonListURLPort)
                .fetchPokemonURLs(any(URI.class));

        var result = fetchAllPokemonIdsUseCase.fetchAllPokemonIds();

        assertThat(result).hasSize(11); // Assuming each page has 10 Pokémon
        verify(pokemonListURLPort, times(2)).fetchPokemonURLs(any(URI.class));
    }

    @Test
    void shouldReturnEmptyList_whenApiFails() {
        when(pokemonListURLPort.fetchPokemonURLs(any(URI.class)))
                .thenReturn(Optional.empty());

        var result = fetchAllPokemonIdsUseCase.fetchAllPokemonIds();

        assertThat(result).isEmpty();
        verify(pokemonListURLPort, times(1)).fetchPokemonURLs(any(URI.class));
    }

    @Test
    void shouldStopPagination_whenNextUrlIsNull() {
        PokemonListURL page = new PokemonListURL(1, null,
                List.of(new PokemonURL("pikachu", "https://pokeapi.co/api/v2/pokemon/25/"))
        );

        when(pokemonListURLPort.fetchPokemonURLs(any(URI.class)))
                .thenReturn(Optional.of(page));

        Collection<Integer> result = fetchAllPokemonIdsUseCase.fetchAllPokemonIds();
        assertThat(result).hasSize(1);

        verify(pokemonListURLPort, times(1)).fetchPokemonURLs(any(URI.class));
    }

    private PokemonListURL parseJsonToPokemonListURL(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, PokemonListURL.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}
