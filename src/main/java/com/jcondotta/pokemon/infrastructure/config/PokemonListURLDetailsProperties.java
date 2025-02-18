package com.jcondotta.pokemon.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pokemon.api.list-url-details")
public record PokemonListURLDetailsProperties(String url, int limit) { }
