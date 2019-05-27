package br.com.netodevel.resiliencert;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ResilienceRestTemplateTest {

    @Test
    public void should_return_not_null() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(RestTemplate.class);
        context.refresh();

        final RestTemplate restTemplate = context.getBean(RestTemplate.class);

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate();
        resilienceRestTemplate.configure(restTemplate);
    }

    @Test
    public void when_retry_enable_then_invoke_proxy() {
        RestOperations restOperations = mock(RestOperations.class);

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate();
        resilienceRestTemplate.configure(new RestTemplate());
        resilienceRestTemplate.configureProxy(restOperations);

        resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                .retry(2)
                .start();

        verify(restOperations, times(1))
                .getForEntity(eq("http://localhost:8080/posts"),
                        anyObject());
    }

    @Test(expected = ResourceAccessException.class)
    public void when_retry_disable_then_not_invoke_proxy() {
        RestOperations restOperations = mock(RestOperations.class);

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate();
        resilienceRestTemplate.configure(new RestTemplate());
        resilienceRestTemplate.configureProxy(restOperations);

        resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                .start();

        verify(restOperations, times(0))
                .getForEntity(eq("http://localhost:8080/posts"),
                        anyObject());
    }

    @Test
    public void when_cache_enable_then_not_invoke_retry() {
        RestOperations restOperations = mock(RestOperations.class);
        CacheManager cacheManager = new CacheManager();
        cacheManager.insertCache("http://localhost:8080/posts",
                new ResponseEntity<>(new PostResponse(), HttpStatus.OK));

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate();
        resilienceRestTemplate.configure(new RestTemplate());
        resilienceRestTemplate.configureProxy(restOperations);
        resilienceRestTemplate.configureCache(cacheManager);

        resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                .cache(Duration.ofSeconds(10))
                .retry(2)
                .start();

        verify(restOperations, times(0))
                .getForEntity(eq("http://localhost:8080/posts"), anyObject());
    }

    @Test
    public void when_cache_enable_then_return_title() {
        RestOperations restOperations = mock(RestOperations.class);

        CacheManager cacheManager = new CacheManager();
        cacheManager.insertCache("http://localhost:8080/posts",
                new ResponseEntity<>(new PostResponse("id", "title-xpto"), HttpStatus.OK));

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate();
        resilienceRestTemplate.configure(new RestTemplate());
        resilienceRestTemplate.configureProxy(restOperations);
        resilienceRestTemplate.configureCache(cacheManager);

        ResponseEntity<PostResponse> response =
                resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                .cache(Duration.ofSeconds(10))
                .retry(2)
                .start();

        assertEquals("title-xpto", response.getBody().getTitle());
    }

}