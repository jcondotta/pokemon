package com.jcondotta.pokemon.application.service;

import com.jcondotta.pokemon.application.ports.out.cache.CacheService;
import com.jcondotta.pokemon.application.usecases.FetchAllPokemonIdsUseCase;
import com.jcondotta.pokemon.application.usecases.FetchPokemonListUseCase;
import com.jcondotta.pokemon.application.usecases.FetchTopRankedPokemonUseCase;
import com.jcondotta.pokemon.application.usecases.dto.PokemonRankingDTO;
import com.jcondotta.pokemon.domain.model.Pokemon;
import com.jcondotta.pokemon.infrastructure.adapters.out.cache.PokemonCacheKeys;
import com.jcondotta.pokemon.infrastructure.adapters.out.cache.PokemonRankingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Qualifier("topTallestPokemonService")
public class FetchTopTallestPokemonService implements FetchTopRankedPokemonUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTopTallestPokemonService.class);

    private final FetchAllPokemonIdsUseCase fetchAllPokemonIdsUseCase;
    private final FetchPokemonListUseCase fetchPokemonListUseCase;
    private final CacheService<String, List<PokemonRankingDTO>> cacheService;

    public FetchTopTallestPokemonService(FetchAllPokemonIdsUseCase fetchAllPokemonIdsUseCase,
                                         FetchPokemonListUseCase fetchPokemonListUseCase,
                                         CacheService<String, List<PokemonRankingDTO>> cacheService) {
        this.fetchAllPokemonIdsUseCase = fetchAllPokemonIdsUseCase;
        this.fetchPokemonListUseCase = fetchPokemonListUseCase;
        this.cacheService = cacheService;
    }

    @Override
    public List<PokemonRankingDTO> getTopRankedPokemon(int topN) {
        var allPokemonIds = fetchAllPokemonIdsUseCase.fetchAllPokemonIds();

        var cacheKey = PokemonCacheKeys.topRanking(PokemonRankingType.TALLEST, topN);
        LOGGER.info("Checking cache for top {} tallest Pokémon...", topN);

        return cacheService.get(cacheKey)
                .orElseGet(() -> {
                    var topTallestPokemon = fetchPokemonListUseCase.fetchPokemonList(allPokemonIds)
                            .stream()
                            .sorted(Comparator.comparingDouble(Pokemon::height).reversed())
                            .limit(topN)
                            .map(pokemon -> new PokemonRankingDTO(pokemon.name(), pokemon.height()))
                            .toList();

                    cacheService.set(cacheKey, topTallestPokemon);
                    return topTallestPokemon;
                });
    }
}
