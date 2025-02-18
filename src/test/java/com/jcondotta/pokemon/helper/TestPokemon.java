package com.jcondotta.pokemon.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.pokemon.domain.model.Pokemon;

public enum TestPokemon {

    CHARIZARD(6,
            """
                {
                  "id": 6,
                  "name": "charizard",
                  "weight": 905.0,
                  "height": 17,
                  "base_experience": 267
                }
            """
    ),
    PIKACHU(25,
            """
                {
                  "id": 25,
                  "name": "pikachu",
                  "weight": 60.0,
                  "height": 4,
                  "base_experience": 112
                }
            """
    ),
    SNORLAX(143,
            """
                {
                  "id": 143,
                  "name": "snorlax",
                  "weight": 4600.0,
                  "height": 21,
                  "base_experience": 189
                }
            """
    ),
    GYARADOS(130,
            """
                {
                  "id": 130,
                  "name": "gyarados",
                  "weight": 2350.0,
                  "height": 65,
                  "base_experience": 189
                }
            """
    ),
    DRAGONITE(149,
            """
                {
                  "id": 149,
                  "name": "dragonite",
                  "weight": 2100.0,
                  "height": 22,
                  "base_experience": 300
                }
            """
    ),
    MEWTWO(150,
            """
                {
                  "id": 150,
                  "name": "mewtwo",
                  "weight": 1220.0,
                  "height": 20,
                  "base_experience": 340
                }
            """
    ),
    GENGAR(94,
            """
                {
                  "id": 94,
                  "name": "gengar",
                  "weight": 405.0,
                  "height": 15,
                  "base_experience": 250
                }
            """
    ),
    KADABRA(64,
            """
                {
                  "id": 64,
                  "name": "kadabra",
                  "weight": 565.0,
                  "height": 13,
                  "base_experience": 140
                }
            """
    ),
    ZAPDOS(145,
            """
                {
                  "id": 145,
                  "name": "zapdos",
                  "weight": 526.0,
                  "height": 16,
                  "base_experience": 261
                }
            """
    ),
    FLAREON(136,
            """
                {
                  "id": 136,
                  "name": "flareon",
                  "weight": 250.0,
                  "height": 9,
                  "base_experience": 184
                }
            """
    ),
    ELECTABUZZ(125,
            """
                {
                  "id": 125,
                  "name": "electabuzz",
                  "weight": 300.0,
                  "height": 11,
                  "base_experience": 172
                }
            """
    );

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final int id;
    private final String details;

    TestPokemon(int id, String details) {
        this.id = id;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public String getDetails() {
        return details;
    }

    public Pokemon pokemonDetailsToPokemon() {
        try {
            return OBJECT_MAPPER.readValue(details, Pokemon.class);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse Pokemon details for " + this.name(), e);
        }
    }
}