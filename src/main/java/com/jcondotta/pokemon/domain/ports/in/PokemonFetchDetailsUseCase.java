package com.jcondotta.pokemon.domain.ports.in;

import com.jcondotta.pokemon.domain.model.Pokemon;

import java.util.Optional;

public interface PokemonFetchDetailsUseCase {
    Optional<Pokemon> fetchById(int pokemonId);
}