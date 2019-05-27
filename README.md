# resilience-rest-template
Resilience operations on top of the RestTemplate of the Spring Framework

### Alert

This project in progress.

### Operations

    * Retry (Done)
    * Cache (Doing)
    * Fallback (Todo)

### Goal

```java

   @Autowired
   private ResilienceRestTemplate resilienceRestTemplate;

   public ResponseEntity<?> getPosts() {
     return resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                .retry(2)
                .cache(Duration.ofSeconds(15))
                .fallback(APIErrorException::new)
                .start();
   }
   
```
