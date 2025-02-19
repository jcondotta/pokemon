package com.jcondotta.pokemon.helper;

import com.jcondotta.pokemon.helper.handler.FetchByIdHandler;
import com.jcondotta.pokemon.helper.handler.PaginatedListHandler;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PokemonMockResponseDispatcher extends Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonMockResponseDispatcher.class);

    @NotNull
    @Override
    public MockResponse dispatch(@NotNull RecordedRequest request) {
        HttpUrl requestUrl = request.getRequestUrl();

        if (requestUrl == null) {
            return new MockResponse()
                    .setResponseCode(400)
                    .setBody("Bad Request: Missing URL");
        }

        String path = requestUrl.encodedPath();

        if (path.matches("/api/v2/pokemon/\\d+")) {
            return new FetchByIdHandler().handleFetchById(path);
        }

        if (path.startsWith("/api/v2/pokemon")) {
            return new PaginatedListHandler().handlePaginatedList(requestUrl);
        }

        return new MockResponse()
                .setResponseCode(404)
                .setBody("Endpoint Not Found");
    }
}
