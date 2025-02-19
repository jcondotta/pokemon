package com.jcondotta.pokemon.application.usecases;

import java.util.Collection;

public interface FetchAllPokemonIdsUseCase {

    Collection<Integer> fetchAllPokemonIds();
}
