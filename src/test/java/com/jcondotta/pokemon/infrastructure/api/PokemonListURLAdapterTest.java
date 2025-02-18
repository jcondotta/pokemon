package com.jcondotta.pokemon.infrastructure.api;

import com.jcondotta.pokemon.helper.TestPokemon;
import com.jcondotta.pokemon.helper.TestPokemonListURL;
import com.jcondotta.pokemon.helper.TestRestClient;
import com.jcondotta.pokemon.service.client.list_urls.PokemonURLResponse;
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
import java.net.URI;
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

    private PokemonListURLAdapter pokemonBatchAPIClient;

    @BeforeEach
    void beforeEach() throws IOException {
        mockWebServer.start();

        var restClient = TestRestClient.builder().build();
        pokemonBatchAPIClient = new PokemonListURLAdapter(restClient);
    }

    @AfterEach
    void afterEach() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnPokemonBatch_whenAPIRespondsSuccessfully() {
        var currentLimit = 2;
        var currentOffset = 0;

        var pokemonListURLResponse = TestPokemonListURL.generatePaginatedResponse(currentLimit, currentOffset);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(pokemonListURLResponse)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        var expectedPokemonNames = List.of(
                TestPokemon.CHARIZARD.pokemonDetailsToPokemon().name(),
                TestPokemon.PIKACHU.pokemonDetailsToPokemon().name()
        );

        URI pokemonListURI = URI.create(mockWebServer
                .url(String.format("/api/v2/pokemon?limit=%s", currentLimit))
                .toString());

        var apiResponse = pokemonBatchAPIClient.fetchPokemonURLs(pokemonListURI);

        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.count()).isEqualTo(TestPokemon.values().length);
        assertThat(apiResponse.results()).hasSize(expectedPokemonNames.size());

        assertThat(apiResponse.results())
                .extracting(PokemonURLResponse::name)
                .containsExactlyElementsOf(expectedPokemonNames);
    }

    @Test
    void shouldReturnNull_whenAPIReturnsNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
                .addHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE));

        var response = pokemonBatchAPIClient.fetchPokemonURLs(URI.create(mockWebServer.url("/api/v2/pokemon?limit=2").toString()));

        assertThat(response).isNull();
    }

    @Test
    void shouldReturnNull_whenAPIReturnsServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE));

        var response = pokemonBatchAPIClient.fetchPokemonURLs(URI.create(mockWebServer.url("/api/v2/pokemon?limit=2").toString()));

        assertThat(response).isNull();
    }

    @Test
    void shouldReturnNull_whenAPITimesOut() {
        String batchResponse = """
        {
            "count": 1304,
            "next": "https://pokeapi.co/api/v2/pokemon?offset=2&limit=2",
            "previous": null,
            "results": [
                {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/"},
                {"name": "ivysaur", "url": "https://pokeapi.co/api/v2/pokemon/2/"}
            ]
        }
        """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(batchResponse)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBodyDelay(150, TimeUnit.MILLISECONDS) // Simulating timeout
        );

        var response = pokemonBatchAPIClient.fetchPokemonURLs(URI.create(mockWebServer.url("/api/v2/pokemon?limit=2").toString()));

        assertThat(response).isNull();
    }
}
