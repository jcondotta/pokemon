package com.jcondotta.pokemon.application.ports.out.api;

import com.jcondotta.pokemon.domain.model.Pokemon;

import java.util.Optional;

public interface PokemonFetchDetailsPort {

    Optional<Pokemon> fetchById(int pokemonId);
}
