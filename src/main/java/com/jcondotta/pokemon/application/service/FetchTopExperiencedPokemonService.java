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
@Qualifier("topExperiencedPokemonService")
public class FetchTopExperiencedPokemonService implements FetchTopRankedPokemonUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTopExperiencedPokemonService.class);

    private final FetchAllPokemonIdsUseCase fetchAllPokemonIdsUseCase;
    private final FetchPokemonListUseCase fetchPokemonListUseCase;
    private final CacheService<String, List<PokemonRankingDTO>> cacheService;

    public FetchTopExperiencedPokemonService(FetchAllPokemonIdsUseCase fetchAllPokemonIdsUseCase,
                                             FetchPokemonListUseCase fetchPokemonListUseCase,
                                             CacheService<String, List<PokemonRankingDTO>> cacheService) {
        this.fetchAllPokemonIdsUseCase = fetchAllPokemonIdsUseCase;
        this.fetchPokemonListUseCase = fetchPokemonListUseCase;
        this.cacheService = cacheService;
    }

    @Override
    public List<PokemonRankingDTO> getTopRankedPokemon(int topN) {
        var allPokemonIds = fetchAllPokemonIdsUseCase.fetchAllPokemonIds();

        var cacheKey = PokemonCacheKeys.topRanking(PokemonRankingType.MOST_EXPERIENCED, topN);
        LOGGER.info("Checking cache for top {} most experienced Pokémon...", topN);

        return cacheService.get(cacheKey)
                .orElseGet(() -> {
                    var topExperiencedPokemon = fetchPokemonListUseCase.fetchPokemonList(allPokemonIds)
                            .stream()
                            .sorted(Comparator.comparingInt(Pokemon::baseExperience).reversed())
                            .limit(topN)
                            .map(pokemon -> new PokemonRankingDTO(pokemon.name(), pokemon.baseExperience()))
                            .toList();

                    cacheService.set(cacheKey, topExperiencedPokemon);
                    return topExperiencedPokemon;
                });
    }
}
