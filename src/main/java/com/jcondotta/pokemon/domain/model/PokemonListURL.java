package com.jcondotta.pokemon.domain.model;

import java.util.List;

public record PokemonListURL(int count, String next, List<PokemonURL> results) {}
