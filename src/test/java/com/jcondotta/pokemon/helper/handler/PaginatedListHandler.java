package com.jcondotta.pokemon.helper.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.pokemon.helper.TestPokemon;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;

import java.util.*;

public class PaginatedListHandler {

    public MockResponse handlePaginatedList(HttpUrl requestUrl) {
        // Ensure requestUrl is not null
        Objects.requireNonNull(requestUrl, "Request URL cannot be null");

        // Parse query parameters safely with default values
        int offset = Optional.ofNullable(requestUrl.queryParameter("offset"))
                .map(Integer::parseInt)
                .orElse(0);

        int limit = Optional.ofNullable(requestUrl.queryParameter("limit"))
                .map(Integer::parseInt)
                .orElse(10);

        int totalCount = TestPokemon.values().length;

        // Build the next URL for pagination safely
        String nextUrl = (offset + limit < totalCount)
                ? requestUrl.newBuilder()
                .setQueryParameter("offset", String.valueOf(offset + limit))
                .setQueryParameter("limit", String.valueOf(limit))
                .build()
                .toString()
                : null;

        // Build clean base URL without query params
        HttpUrl cleanBaseUrl = requestUrl.newBuilder()
                .query(null)  // Remove query parameters
                .build();

        // Build results for the current page
        List<Map<String, String>> results = Arrays.stream(TestPokemon.values())
                .skip(offset)
                .limit(limit)
                .map(pokemon -> Map.of(
                        "name", pokemon.name(),
                        "url", cleanBaseUrl.newBuilder()
                                .addPathSegment(String.valueOf(pokemon.getId()))
                                .build()
                                .toString()
                ))
                .toList();

        Map<String, Object> response = Map.ofEntries(
                Map.entry("count", totalCount),
                Map.entry("next", nextUrl != null ? nextUrl : ""),  // Replace null with empty string
                Map.entry("results", results)
        );


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = objectMapper.writeValueAsString(response);

            return new MockResponse()
                    .setResponseCode(200)
                    .setBody(responseBody)
                    .addHeader("Content-Type", "application/json");
        } catch (JsonProcessingException e) {
            return new MockResponse()
                    .setResponseCode(500)
                    .setBody("Failed to generate paginated response");
        }
    }
}
