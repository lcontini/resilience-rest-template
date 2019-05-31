package br.com.netodevel.resiliencert;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ResilienceRestTemplateIntegrationTest {

    private AnnotationConfigApplicationContext context;

    @Before
    public void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.register(RestTemplate.class);
        context.register(ResilienceRestTemplateAutoConfiguration.class);
        context.refresh();
    }

    @Test
    public void should_return_response_entity() {
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        ResilienceRestTemplate resilienceRestTemplate = context.getBean(ResilienceRestTemplate.class);

        MockRestServiceServer mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("id", "XYZ");
        mockResponse.put("title", "Title");

        mockRestServiceServer.expect(requestTo("http://localhost:8080/posts"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockResponse.toString(), MediaType.APPLICATION_JSON));

        ResponseEntity<PostResponse> response = resilienceRestTemplate
                .getForEntity("http://localhost:8080/posts", PostResponse.class)
                .call();

        assertNotNull(response);
    }

    @Test
    public void given_retry_enable_should_return_response_entity() {
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        ResilienceRestTemplate resilienceRestTemplate = context.getBean(ResilienceRestTemplate.class);

        MockRestServiceServer mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("id", "XYZ");
        mockResponse.put("title", "Title");

        mockRestServiceServer.expect(requestTo("http://localhost:8080/posts"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockResponse.toString(), MediaType.APPLICATION_JSON));

        ResponseEntity<PostResponse> response = resilienceRestTemplate
                .getForEntity("http://localhost:8080/posts", PostResponse.class)
                .retry(2)
                .call();

        assertNotNull(response);
    }

}
