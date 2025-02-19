package com.jcondotta.pokemon.web.controller;

import com.jcondotta.pokemon.application.usecases.dto.PokemonRankingDTO;
import com.jcondotta.pokemon.application.usecases.dto.PokemonRankingListDTO;
import com.jcondotta.pokemon.domain.model.Pokemon;
import com.jcondotta.pokemon.helper.PokemonMockResponseDispatcher;
import com.jcondotta.pokemon.helper.TestPokemon;
import com.jcondotta.pokemon.infrastructure.adapters.in.api.PokemonAPIPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TopHeaviestPokemonControllerIT {

    private static final MockWebServer MOCK_WEB_SERVER = new MockWebServer();
    private static final Integer DEFAULT_TOP_N = 5;

    private RequestSpecification requestSpecification;

    private static final List<Pokemon> sortedTestPokemonByWeight = Arrays.stream(TestPokemon.values())
            .map(TestPokemon::pokemonDetailsToPokemon)
            .sorted(Comparator.comparingDouble(Pokemon::weight).reversed())
            .toList();

    @BeforeAll
    static void beforeAll() throws IOException {
        MOCK_WEB_SERVER.start();
        MOCK_WEB_SERVER.setDispatcher(new PokemonMockResponseDispatcher());
    }

    @AfterAll
    static void afterAll() throws IOException {
        MOCK_WEB_SERVER.shutdown();
    }

    @BeforeEach
    void beforeEach(@LocalServerPort int port) {
        requestSpecification = RestAssured.given()
                .baseUri("http://localhost:" + port)
                .basePath(PokemonAPIPath.POKEMON_TOP_HEAVIEST)
                .contentType(ContentType.JSON);
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("pokemon.api.fetch-by-id.url",
                () -> MOCK_WEB_SERVER.url("/api/v2/pokemon/") + "{id}"
        );

        registry.add("pokemon.api.list-url.url",
                () -> MOCK_WEB_SERVER.url("/api/v2/pokemon").toString()
        );
    }

    @Test
    void shouldReturnTop5HeaviestPokemon_whenValidRequest() {
        var response = given()
                .spec(requestSpecification)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(PokemonRankingListDTO.class);

        var expectedTop5Names = sortedTestPokemonByWeight.stream()
                .limit(DEFAULT_TOP_N)
                .map(Pokemon::name)
                .toList();

        assertThat(response.getPokemonList())
                .hasSize(DEFAULT_TOP_N)
                .extracting(PokemonRankingDTO::name)
                .containsExactlyElementsOf(expectedTop5Names);
    }

    @Test
    void shouldReturnTopNHeaviestPokemon_whenTopNIsProvided() {
        int customTopN = 3;

        var response = given()
                .spec(requestSpecification)
                .queryParam("topN", customTopN)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(PokemonRankingListDTO.class);

        var expectedTopNNames = sortedTestPokemonByWeight.stream()
                .limit(customTopN)
                .map(Pokemon::name)
                .toList();

        assertThat(response.getPokemonList())
                .hasSize(customTopN)
                .extracting(PokemonRankingDTO::name)
                .containsExactlyElementsOf(expectedTopNNames);
    }
}