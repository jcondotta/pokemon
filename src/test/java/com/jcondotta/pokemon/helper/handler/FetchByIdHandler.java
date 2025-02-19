package com.jcondotta.pokemon.helper.handler;

import com.jcondotta.pokemon.helper.TestPokemon;
import okhttp3.mockwebserver.MockResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class FetchByIdHandler {

    public MockResponse handleFetchById(String path) {
        try {
            var pokemonId = extractIdFromPath(path)
                    .orElseThrow(() -> new NumberFormatException("Invalid Pokémon ID"));

            return Arrays.stream(TestPokemon.values())
                    .filter(testPokemon -> testPokemon.getId() == pokemonId)
                    .findFirst()
                    .map(this::buildMockResponse)
                    .orElseGet(() -> new MockResponse().setResponseCode(404));
        }
        catch (NumberFormatException e) {
            return new MockResponse()
                    .setResponseCode(400).setBody("Invalid Pokémon ID");
        }
    }

    private MockResponse buildMockResponse(TestPokemon testPokemon){
        return new MockResponse()
                .setResponseCode(200)
                .setBody(testPokemon.getDetails())
                .addHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
    }

    public static Optional<Integer> extractIdFromPath(String url) {
        try {
            String cleanUrl = url.split("\\?")[0];

            String idStr = cleanUrl.substring(cleanUrl.lastIndexOf("/") + 1);

            return Optional.of(Integer.parseInt(idStr));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }
}
