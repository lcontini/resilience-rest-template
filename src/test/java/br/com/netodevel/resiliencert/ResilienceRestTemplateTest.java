package br.com.netodevel.resiliencert;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

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

        resilienceRestTemplate.get("http://localhost:8080/posts", PostResponse.class)
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

        resilienceRestTemplate.get("http://localhost:8080/posts", PostResponse.class)
                .start();

        verify(restOperations, times(0))
                .getForEntity(eq("http://localhost:8080/posts"),
                        anyObject());
    }

}