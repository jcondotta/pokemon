package com.jcondotta.pokemon.domain.ports.out;

import com.jcondotta.pokemon.domain.model.Pokemon;

import java.util.Optional;

public interface PokemonFetchDetailsPort {

    Optional<Pokemon> fetchById(int pokemonId);
}
