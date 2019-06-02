# resilience-rest-template
Resilience operations on top of the RestTemplate of the Spring Framework

### Alert

This project in progress.

### Operations

    * Retry (Done)
    * Cache (Done)
    * Fallback (Doing)

### Retry 

```java

@RestController
@RequestMapping(value = "/pokemon-species")
public class PokemonController {

    @Autowired
    private ResilienceRestTemplate resilienceRestTemplate;

    @GetMapping
    public ResponseEntity<?> getPokemonSpecies() {
        ResponseEntity<PokemonResponse> pokemonsResponse = resilienceRestTemplate
                .getForEntity("https://pokeapi.co/api/v2/pokemon-species/", PokemonResponse.class)
                .retry(2)
                .call();
        return new ResponseEntity<>(pokemonsResponse.getBody(), HttpStatus.OK);
   }
}
   
```

### Cache

```java

@RestController
@RequestMapping(value = "/pokemon-species")
public class PokemonController {

    @Autowired
    private ResilienceRestTemplate resilienceRestTemplate;

    @GetMapping
    public ResponseEntity<?> getPokemonSpecies() {
        ResponseEntity<PokemonResponse> pokemonsResponse = resilienceRestTemplate
                .getForEntity("https://pokeapi.co/api/v2/pokemon-species/", PokemonResponse.class)
                .cache(Duration.ofSeconds(30))
                .call();
        return new ResponseEntity<>(pokemonsResponse.getBody(), HttpStatus.OK);
   }
}
   
```

Cache logs

```
b.c.n.resiliencert.CacheScheduler   : cache started for the key: https://pokeapi.co/api/v2/pokemon-species/
```

```
b.c.n.resiliencert.CacheScheduler   : cache finalized for the key: https://pokeapi.co/api/v2/pokemon-species/
```


### Fallback

```java
@RestController
@RequestMapping(value = "/pokemon-species")
public class PokemonController {

    @Autowired
    private ResilienceRestTemplate resilienceRestTemplate;

    @GetMapping
    public ResponseEntity<?> getPokemonSpecies() {
       PokemonResponse defResponse = new PokemonResponse();
       defResponse.setResults(asList(new PokemonSpecies("fake_name", "fake_url"),
                                     new PokemonSpecies("fake_2", "url_2")));

       ResponseEntity<PokemonResponse> pokemonResponse = resilienceRestTemplate
                .getForEntity("https://pokeapi.co/api/v2/pokemon-species/", PokemonResponse.class)
                .fallback(defResponse)
                .call();
        return new ResponseEntity<>(pokemonsResponse.getBody(), HttpStatus.OK);
   }
}
```
