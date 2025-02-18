package com.jcondotta.pokemon.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pokemon(
        int id,

        String name,

        double weight,

        int height,

        @JsonProperty("base_experience")
        int baseExperience
) {}