# resilience-rest-template
Resilience operations on top of the RestTemplate of the Spring Framework

### Alert

This project in progress.

### Operations

    * Retry (doing)
    * Cache (Todo)
    * Fallback (Todo)

### Retry Sample

```java
resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                .retry(2)
                .start();
```