package com.jcondotta.pokemon.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pokemon.api.list-url")
public record PokemonListURLProperties(String url, int limit) { }
