package br.com.netodevel.resiliencert;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ResilienceRestTemplateTest {

    @Test
    public void should_return_not_null() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(RestTemplate.class);
        context.refresh();

        final RestTemplate restTemplate = context.getBean(RestTemplate.class);

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate(restTemplate);
        assertNotNull(resilienceRestTemplate.getRestTemplate());
    }

    @Test
    public void when_retry_enable_then_invoke_proxy() {
        RestOperations restOperations = mock(RestOperations.class);

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate(new RestTemplate(), restOperations);
        resilienceRestTemplate.configureJacksonConvert();

        resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                .retry(2)
                .call();

        verify(restOperations, times(1))
                .getForEntity(eq("http://localhost:8080/posts"),
                        anyObject());
    }

    @Test(expected = ResourceAccessException.class)
    public void when_retry_disable_then_not_invoke_proxy() {
        RestOperations restOperations = mock(RestOperations.class);

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate(new RestTemplate(), restOperations);
        resilienceRestTemplate.configureJacksonConvert();

        resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                .call();

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

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate(new RestTemplate(), restOperations, cacheManager);
        resilienceRestTemplate.configureJacksonConvert();

        resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                .cache(Duration.ofSeconds(10))
                .retry(2)
                .call();

        verify(restOperations, times(0))
                .getForEntity(eq("http://localhost:8080/posts"), anyObject());
    }

    @Test
    public void when_cache_enable_then_return_title() {
        RestOperations restOperations = mock(RestOperations.class);

        CacheManager cacheManager = new CacheManager();
        cacheManager.insertCache("http://localhost:8080/posts",
                new ResponseEntity<>(new PostResponse("id", "title-xpto"), HttpStatus.OK));

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate(new RestTemplate(), restOperations, cacheManager);
        resilienceRestTemplate.configureJacksonConvert();

        ResponseEntity<PostResponse> response =
                resilienceRestTemplate.getForEntity("http://localhost:8080/posts", PostResponse.class)
                        .cache(Duration.ofSeconds(10))
                        .retry(2)
                        .call();

        assertEquals("title-xpto", response.getBody().getTitle());
    }

    @Test
    public void when_request_success_should_save_in_cache() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(RestTemplate.class);
        context.register(ResilienceRestTemplateAutoConfiguration.class);
        context.refresh();

        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        ResilienceRestTemplate resilienceRestTemplate = context.getBean(ResilienceRestTemplate.class);

        MockRestServiceServer mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("id", "XYZ");
        mockResponse.put("title", "Title");

        mockRestServiceServer.expect(requestTo("http://localhost:8080/posts"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockResponse.toString(), MediaType.APPLICATION_JSON));

        resilienceRestTemplate
                .getForEntity("http://localhost:8080/posts", PostResponse.class)
                .cache(Duration.ofSeconds(3))
                .call();

        PostResponse postResponse = (PostResponse) resilienceRestTemplate.getCacheManager().getCacheValue("http://localhost:8080/posts");
        assertEquals("Title", postResponse.getTitle());
    }

    @Test
    public void when_cache_expired_should_return_null() throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(RestTemplate.class);
        context.register(ResilienceRestTemplateAutoConfiguration.class);
        context.refresh();

        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        ResilienceRestTemplate resilienceRestTemplate = context.getBean(ResilienceRestTemplate.class);

        MockRestServiceServer mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("id", "XYZ");
        mockResponse.put("title", "Title");

        mockRestServiceServer.expect(requestTo("http://localhost:8080/posts"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockResponse.toString(), MediaType.APPLICATION_JSON));

        resilienceRestTemplate
                .getForEntity("http://localhost:8080/posts", PostResponse.class)
                .cache(Duration.ofSeconds(3))
                .call();

        Thread.sleep(3100);
        assertNull(resilienceRestTemplate.getCacheManager().getCacheValue("http://localhost:8080/posts"));
    }

    @Test
    public void when_cache_not_expired_should_return_value() throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(RestTemplate.class);
        context.register(ResilienceRestTemplateAutoConfiguration.class);
        context.refresh();

        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        ResilienceRestTemplate resilienceRestTemplate = context.getBean(ResilienceRestTemplate.class);

        MockRestServiceServer mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("id", "XYZ");
        mockResponse.put("title", "Title");

        mockRestServiceServer.expect(requestTo("http://localhost:8080/posts"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockResponse.toString(), MediaType.APPLICATION_JSON));

        resilienceRestTemplate
                .getForEntity("http://localhost:8080/posts", PostResponse.class)
                .cache(Duration.ofSeconds(3))
                .call();

        Thread.sleep(2000);
        assertNotNull(resilienceRestTemplate.getCacheManager().getCacheValue("http://localhost:8080/posts"));
    }

}