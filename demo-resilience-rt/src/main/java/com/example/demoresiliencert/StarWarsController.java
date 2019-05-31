package com.example.demoresiliencert;

import br.com.netodevel.resiliencert.ResilienceRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/movies")
public class StarWarsController {

    @Autowired
    private ResilienceRestTemplate resilienceRestTemplate;

    @GetMapping
    public ResponseEntity<?> getMovies() {
        ResponseEntity<FilmsResponse> filmsResponse = resilienceRestTemplate
                .getForEntity("https://swapi.co/api/films/", FilmsResponse.class)
                .call();
        return new ResponseEntity<>(filmsResponse, HttpStatus.OK);
    }

}
