package com.example.demoresiliencert;

import br.com.netodevel.resiliencert.ResilienceRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping(value = "/movies")
public class PokemonController {

    @Autowired
    private ResilienceRestTemplate resilienceRestTemplate;

    @GetMapping
    public ResponseEntity<?> getPokemonSpecies() {
        Timer timer = new Timer();

        ResponseEntity<PokemonResponse> filmsResponse = resilienceRestTemplate
                .getForEntity("https://pokeapi.co/api/v2/pokemon-species/", PokemonResponse.class)
                .cache(Duration.ofSeconds(30))
                .call();

        System.out.println("ms: " + timer.toString());
        return new ResponseEntity<>(filmsResponse.getBody(), HttpStatus.OK);
    }

}
