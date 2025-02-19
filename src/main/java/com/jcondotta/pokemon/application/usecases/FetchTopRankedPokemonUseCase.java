package com.jcondotta.pokemon.application.usecases;

import com.jcondotta.pokemon.application.usecases.dto.PokemonRankingDTO;

import java.util.Collection;

public interface FetchTopRankedPokemonUseCase {

    Collection<PokemonRankingDTO> getTopRankedPokemon(int topN);
}
