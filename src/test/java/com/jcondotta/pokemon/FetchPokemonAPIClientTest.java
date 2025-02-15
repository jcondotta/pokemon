package com.jcondotta.pokemon;

import com.jcondotta.pokemon.service.FetchPokemonAPIClient;
import com.jcondotta.pokemon.service.client.exceptions.PokemonExternalAPIException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

class FetchPokemonAPIClientTest {

    private final static MockWebServer MOCK_WEB_SERVER = new MockWebServer();

    private static final int PIKACHU_ID = TestPokemon.PIKACHU.getId();
    private static final String PIKACHU_DETAILS = TestPokemon.PIKACHU.getDetails();

    private FetchPokemonAPIClient fetchPokemonAPIClient;

    @BeforeAll
    static void beforeAll() throws IOException {
        MOCK_WEB_SERVER.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        MOCK_WEB_SERVER.shutdown();
    }

    @BeforeEach
    void beforeEach() {
        var restClient = RestClient.create();

        String baseUrl = MOCK_WEB_SERVER.url("/api/v2/pokemon/").toString(); //TODO MUDAR ALGO AQUI
        fetchPokemonAPIClient = new FetchPokemonAPIClient(restClient, baseUrl + "{id}");
    }

    @Test
    void shouldReturnPokemonDetails_whenPokemonExists() {
        MOCK_WEB_SERVER.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(PIKACHU_DETAILS)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        var pikachu = fetchPokemonAPIClient.fetchPokemonById(PIKACHU_ID);

        assertAll("Validating Pikachu properties",
                () -> assertThat(pikachu.id()).isEqualTo(PIKACHU_ID),
                () -> assertThat(pikachu.name()).isEqualTo("pikachu"),
                () -> assertThat(pikachu.weight()).isEqualTo(60.0),
                () -> assertThat(pikachu.height()).isEqualTo(4)
        );
    }

    @Test
    void shouldThrowException_whenPokemonNotFound() {
        var expectedErrorMessage = "Pokémon API request failed with status 404 NOT_FOUND while fetching Pokémon id: " + Integer.MAX_VALUE;

        MOCK_WEB_SERVER.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
                .addHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE));

        var nonExistentPokemonId = Integer.MAX_VALUE;

        assertThatThrownBy(() -> fetchPokemonAPIClient.fetchPokemonById(nonExistentPokemonId))
                .isInstanceOf(PokemonExternalAPIException.class)
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    void shouldThrowException_whenPokemonAPIHasServerError() {
        var expectedErrorMessage = "Pokémon API request failed with status 500 INTERNAL_SERVER_ERROR while fetching Pokémon id: " + PIKACHU_ID;

        MOCK_WEB_SERVER.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE));

        assertThatThrownBy(() -> fetchPokemonAPIClient.fetchPokemonById(PIKACHU_ID))
                .isInstanceOf(PokemonExternalAPIException.class)
                .hasMessageContaining(expectedErrorMessage);
    }

}
