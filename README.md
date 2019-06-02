# resilience-rest-template
Resilience operations on top of the RestTemplate of the Spring Framework

### Alert

This project in progress.

### Operations

    * Retry (Done)
    * Cache (Done)
    * Fallback (Todo)

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

When started request with cache:

Log when started

```
INFO 21868 --- [nio-8080-exec-1] b.c.n.resiliencert.CacheScheduler        : cache started for the key: https://pokeapi.co/api/v2/pokemon-species/
```

Log when finish

```
INFO 21868 --- [       Thread-4] b.c.n.resiliencert.CacheScheduler        : cache finalized for the key: https://pokeapi.co/api/v2/pokemon-species/
```

