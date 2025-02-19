package com.jcondotta.pokemon.application.usecases;

import com.jcondotta.pokemon.domain.model.Pokemon;

import java.util.Optional;

public interface FetchPokemonDetailsUseCase {
    Optional<Pokemon> fetchById(int pokemonId);
}