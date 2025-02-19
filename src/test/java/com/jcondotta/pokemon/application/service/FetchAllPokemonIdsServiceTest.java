package com.jcondotta.pokemon.application.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FetchAllPokemonIdsServiceTest {

//    private final PokemonListURLProperties pokemonListURLProperties =
//            new PokemonListURLProperties("https://pokeapi.co/api/v2/pokemon", 2);
//
//    @Mock
//    private PokemonListURLPort pokemonListURLPort;
//
//    private FetchAllPokemonIdsUseCase fetchAllPokemonIdsUseCase;
//
//    @BeforeEach
//    void setUp() {
//        fetchAllPokemonIdsUseCase = new FetchAllPokemonIdsService(pokemonListURLPort, pokemonListURLProperties);
//    }
//
//    @Test
//    void shouldFetchAllPokemonIds_whenPaginationIsSuccessful() {
//        // Arrange: Use TestPokemonListURL to generate paginated responses
//        String firstPageJson = TestPokemonListURL.generatePaginatedResponse(10, 0);
//        String secondPageJson = TestPokemonListURL.generatePaginatedResponse(10, 10); // Next page with offset 2
//
//        // Parse JSON into PokemonListURL objects (assuming a utility to parse JSON)
//        Optional<PokemonListURL> firstPage = Optional.ofNullable(parseJsonToPokemonListURL(firstPageJson));
//        Optional<PokemonListURL> secondPage = Optional.ofNullable(parseJsonToPokemonListURL(secondPageJson));
//
//        doReturn(firstPage)
//                .doReturn(secondPage)
//                .when(pokemonListURLPort)
//                .fetchPokemonURLs(any(URI.class));
//
//        // Act: Execute the method under test
//        Collection<Integer> result = fetchAllPokemonIdsUseCase.fetchAllPokemonIds();
//
//        // Assert: Verify that all expected Pokémon IDs were fetched
//        assertThat(result)
//                .hasSize(11); // Assuming each page has 2 Pokémon
////                .containsExactlyInAnyOrder(1, 2, 3, 4); // Adjust IDs based on generated response
//
//        // Verify that the API was called twice (for both pages)
//        verify(pokemonListURLPort, times(2)).fetchPokemonURLs(any(URI.class));
//    }
//
//    private PokemonListURL parseJsonToPokemonListURL(String json) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.readValue(json, PokemonListURL.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Failed to parse JSON", e);
//        }
//    }

//
//    @Test
//    void shouldHandleApiFailureGracefully() {
//        // Simulate API failure on first call
//        when(pokemonListURLPort.fetchPokemonURLs(any(URI.class)))
//                .thenReturn(null);
//
//        // Execute
//        List<Integer> result = fetchAllPokemonIdsService.fetchAllPokemonIds();
//
//        // Assertions
//        assertTrue(result.isEmpty(), "Result should be empty on API failure");
//
//        // Verify API call
//        verify(pokemonListURLPort, times(1)).fetchPokemonURLs(any(URI.class));
//    }
//
//    @Test
//    void shouldExtractPokemonIdFromUrlSuccessfully() {
//        // Private method, testing via public flow
//
//        // Mock single page response
//        PokemonListURL page = new PokemonListURL(
//                1,
//                null,
//                null,
//                List.of(new PokemonURL("charmander", "https://pokeapi.co/api/v2/pokemon/4/"))
//        );
//
//        when(pokemonListURLPort.fetchPokemonURLs(any(URI.class)))
//                .thenReturn(page);
//
//        // Execute
//        List<Integer> result = fetchAllPokemonIdsService.fetchAllPokemonIds();
//
//        // Assertions
//        assertEquals(1, result.size());
//        assertEquals(4, result.get(0));
//    }
//
//    @Test
//    void shouldStopPaginationWhenNextIsNull() {
//        // Mock single page with no next link
//        PokemonListURL page = new PokemonListURL(
//                1,
//                null,
//                null,
//                List.of(new PokemonURL("pikachu", "https://pokeapi.co/api/v2/pokemon/25/"))
//        );
//
//        when(pokemonListURLPort.fetchPokemonURLs(any(URI.class)))
//                .thenReturn(page);
//
//        // Execute
//        List<Integer> result = fetchAllPokemonIdsService.fetchAllPokemonIds();
//
//        // Assertions
//        assertEquals(1, result.size());
//        assertEquals(25, result.get(0));
//
//        // Verify only one API call was made
//        verify(pokemonListURLPort, times(1)).fetchPokemonURLs(any(URI.class));
//    }
//
//    @Test
//    void shouldLogErrorWhenInvalidPokemonIdInUrl() {
//        // Mock response with invalid URL
//        PokemonListURL page = new PokemonListURL(
//                1,
//                null,
//                null,
//                List.of(new PokemonURL("unknown", "https://pokeapi.co/api/v2/pokemon/not-a-number/"))
//        );
//
//        when(pokemonListURLPort.fetchPokemonURLs(any(URI.class)))
//                .thenReturn(page);
//
//        // Execute and expect NumberFormatException
//        assertThrows(NumberFormatException.class, () -> fetchAllPokemonIdsService.fetchAllPokemonIds());
//    }
}
