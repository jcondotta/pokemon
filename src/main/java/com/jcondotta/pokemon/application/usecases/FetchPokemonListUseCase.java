package com.jcondotta.pokemon.application.usecases;

import com.jcondotta.pokemon.domain.model.Pokemon;

import java.util.Collection;
import java.util.List;

public interface FetchPokemonListUseCase {

    List<Pokemon> fetchPokemonList(Collection<Integer> pokemonIds);

}
