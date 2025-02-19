package com.jcondotta.pokemon.infrastructure.adapters.out.api;

import com.jcondotta.pokemon.application.ports.out.api.PokemonListURLPort;
import com.jcondotta.pokemon.domain.model.PokemonURL;
import com.jcondotta.pokemon.helper.TestPokemon;
import com.jcondotta.pokemon.helper.TestPokemonListURL;
import com.jcondotta.pokemon.helper.TestRestClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
class PokemonListURLAdapterTest {

    private final MockWebServer mockWebServer = new MockWebServer();
    private final static Integer DEFAULT_LIMIT = 2;
    private final static Integer DEFAULT_OFFSET = 0;

    private final static String DEFAULT_LIST_URL = String.format("/api/v2/pokemon?limit=%d&offset=%d",
            DEFAULT_LIMIT, DEFAULT_OFFSET);

    private PokemonListURLPort pokemonListURLPort;

    @BeforeEach
    void beforeEach() throws IOException {
        mockWebServer.start();
        var restClient = TestRestClient.builder()
                .readTimeout(100)
                .build();

        pokemonListURLPort = new PokemonListURLAdapter(restClient);
    }

    @AfterEach
    void afterEach() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnPokemonBatch_whenAPIRespondsSuccessfully() {
        var pokemonListURLResponse = TestPokemonListURL.generatePaginatedResponse(DEFAULT_LIMIT, DEFAULT_OFFSET);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(pokemonListURLResponse)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        var expectedPokemonNames = List.of(
                TestPokemon.CHARIZARD.pokemonDetailsToPokemon().name(),
                TestPokemon.PIKACHU.pokemonDetailsToPokemon().name()
        );

        var pokemonListURI = mockWebServer.url(DEFAULT_LIST_URL).uri();
        var response = pokemonListURLPort.fetchPokemonURLs(pokemonListURI);

        assertThat(response)
                .hasValueSatisfying(apiResponse -> {
                    assertThat(apiResponse.count()).isEqualTo(TestPokemon.values().length);
                    assertThat(apiResponse.results()).hasSize(expectedPokemonNames.size());
                    assertThat(apiResponse.results())
                            .extracting(PokemonURL::name)
                            .containsExactlyElementsOf(expectedPokemonNames);
                });
    }

    @Test
    void shouldReturnNull_whenAPIReturnsNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
                .addHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE));

        var pokemonListURI = mockWebServer.url(DEFAULT_LIST_URL).uri();
        var response = pokemonListURLPort.fetchPokemonURLs(pokemonListURI);

        assertThat(response).isEmpty();
    }

    @Test
    void shouldReturnNull_whenAPIReturnsServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE));

        var pokemonListURI = mockWebServer.url(DEFAULT_LIST_URL).uri();
        var response = pokemonListURLPort.fetchPokemonURLs(pokemonListURI);

        assertThat(response).isEmpty();
    }

    @Test
    void shouldReturnNull_whenAPITimesOut() {
        var pokemonListURLResponse = TestPokemonListURL.generatePaginatedResponse(DEFAULT_LIMIT, DEFAULT_OFFSET);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(pokemonListURLResponse)
                .setBodyDelay(150, TimeUnit.MILLISECONDS)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        var pokemonListURI = mockWebServer.url(DEFAULT_LIST_URL).uri();
        var response = pokemonListURLPort.fetchPokemonURLs(pokemonListURI);

        assertThat(response).isEmpty();
    }
}
