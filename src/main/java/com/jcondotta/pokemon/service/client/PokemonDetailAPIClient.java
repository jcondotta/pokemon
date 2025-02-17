package com.jcondotta.pokemon.service.client;

import com.jcondotta.pokemon.model.Pokemon;

import java.util.Optional;

public interface PokemonDetailAPIClient {

    Optional<Pokemon> fetchById(int pokemonId);
}
