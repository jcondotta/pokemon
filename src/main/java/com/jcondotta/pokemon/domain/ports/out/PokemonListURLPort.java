package com.jcondotta.pokemon.domain.ports.out;

import com.jcondotta.pokemon.service.client.list_urls.PokemonListURLResponse;

import java.net.URI;

public interface PokemonListURLPort {

    PokemonListURLResponse fetchPokemonURLs(URI uri);
}
