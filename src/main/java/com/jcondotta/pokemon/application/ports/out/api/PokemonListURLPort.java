package com.jcondotta.pokemon.application.ports.out.api;

import com.jcondotta.pokemon.domain.model.PokemonListURL;

import java.net.URI;
import java.util.Optional;

public interface PokemonListURLPort {

    Optional<PokemonListURL> fetchPokemonURLs(URI uri);
}
