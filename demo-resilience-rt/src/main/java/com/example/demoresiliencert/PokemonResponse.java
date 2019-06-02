package com.example.demoresiliencert;

import java.util.List;

public class PokemonResponse {

    private List<PokemonSpecies> results;

    public List<PokemonSpecies> getResults() {
        return results;
    }

    public void setResults(List<PokemonSpecies> results) {
        this.results = results;
    }
}
