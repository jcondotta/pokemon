package com.jcondotta.pokemon.infrastructure.adapters.out.api;

import com.jcondotta.pokemon.application.ports.out.api.PokemonFetchDetailsPort;
import com.jcondotta.pokemon.helper.TestPokemon;
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
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
class PokemonDetailFetcherAdapterTest {

    private final MockWebServer mockWebServer = new MockWebServer();

    private static final int PIKACHU_ID = TestPokemon.PIKACHU.getId();
    private static final String PIKACHU_DETAILS = TestPokemon.PIKACHU.getDetails();

    private PokemonFetchDetailsPort pokemonFetchDetailsPort;

    @BeforeEach
    void beforeEach() throws IOException {
        mockWebServer.start();
        var restClient = TestRestClient.builder()
                .readTimeout(100)
                .build();

        var fetchPokemonURI = mockWebServer.url("/api/v2/pokemon/{id}").uri();
        pokemonFetchDetailsPort = new PokemonFetchDetailAdapter(restClient, fetchPokemonURI.toString());
    }

    @AfterEach
    void afterEach() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnPokemon_whenPokemonExists() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(PIKACHU_DETAILS)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        assertThat(pokemonFetchDetailsPort.fetchById(PIKACHU_ID))
                .hasValueSatisfying(pikachu -> assertAll("Validating Pikachu properties",
                        () -> assertThat(pikachu.id()).isEqualTo(PIKACHU_ID),
                        () -> assertThat(pikachu.name()).isEqualTo("pikachu"),
                        () -> assertThat(pikachu.weight()).isEqualTo(60.0),
                        () -> assertThat(pikachu.height()).isEqualTo(4)
                ));
    }

    @Test
    void shouldReturnEmptyOptional_whenPokemonIsNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
                .addHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE));

        var nonExistentPokemonId = -1;

        assertThat(pokemonFetchDetailsPort.fetchById(nonExistentPokemonId)).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_whenPokemonAPIHasServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE));

        assertThat(pokemonFetchDetailsPort.fetchById(PIKACHU_ID)).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_whenPokemonAPITimesOut() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(PIKACHU_DETAILS)
                .setBodyDelay(150, TimeUnit.MILLISECONDS)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        );

        assertThat(pokemonFetchDetailsPort.fetchById(PIKACHU_ID))
                .isEmpty();
    }
}
