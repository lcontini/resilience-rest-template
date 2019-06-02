package com.example.demoresiliencert;

import br.com.netodevel.resiliencert.ResilienceRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static java.util.Arrays.asList;

@RestController
@RequestMapping(value = "/pokemon")
public class PokemonController {

    @Autowired
    private ResilienceRestTemplate resilienceRestTemplate;

    @GetMapping("/cache")
    public ResponseEntity<?> cacheEx() {
        Timer timer = new Timer();

        ResponseEntity<PokemonResponse> pokemonResponse = resilienceRestTemplate
                .getForEntity("https://pokeapi.co/api/v2/pokemon-species/", PokemonResponse.class)
                .cache(Duration.ofSeconds(30))
                .call();

        System.out.println("ms: " + timer.toString());
        return new ResponseEntity<>(pokemonResponse.getBody(), HttpStatus.OK);
    }

    @GetMapping("/fallback")
    public ResponseEntity<?> fallbackEx() {
        Timer timer = new Timer();

        PokemonResponse defResponse = new PokemonResponse();
        defResponse.setResults(asList(new PokemonSpecies("fake_name", "fake_url"), new PokemonSpecies("fake_2", "url_2")));

        ResponseEntity<PokemonResponse> pokemonResponse = resilienceRestTemplate
                .getForEntity("https://xxx.com.br", PokemonResponse.class)
                .fallback(defResponse)
                .call();

        System.out.println("ms: " + timer.toString());
        return new ResponseEntity<>(pokemonResponse.getBody(), HttpStatus.OK);
    }

}
