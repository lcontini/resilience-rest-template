package br.com.netodevel.resiliencert;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * This class represent a wrapper for resilience operations
 * on top of RestTemplate
 */
public class ResilienceRestTemplate {

    /**
     * Mapped options of your request
     */
    private RequestTracker requestTracker;

    /**
     * Rest Template of Spring
     */
    private RestTemplate restTemplate;

    /**
     * Interface for proxy rest template
     */
    private RestOperations restOperationToProxy;

    /**
     * Cache Manager
     */
    private CacheManager cacheManager;

    public void configureCache(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void configureProxy(RestOperations restOperations) {
        this.restOperationToProxy = restOperations;
    }

    public void configure(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        restTemplate.setMessageConverters(MessageConvert.convertJackson());
    }

    public <T> ResilienceRestTemplate getForEntity(String url, Class<T> response) {
        requestTracker = new RequestTracker();
        requestTracker.setUrl(url);
        requestTracker.setResponseClass(response);
        return this;
    }

    public ResilienceRestTemplate retry(Integer numberOfAttempts) {
        requestTracker.setRetryEnable(true);
        requestTracker.setNumberOfAttempts(numberOfAttempts);
        return this;
    }

    public <T> ResponseEntity<T> start() throws RestClientException {
        if (requestTracker.getCacheEnable()) {
            Object object = cacheManager.getCacheValue(requestTracker.getUrl());
            if (object != null) return (ResponseEntity<T>) object;
        }

        if (requestTracker.getRetryEnable()) {
            return restOperationToProxy.getForEntity(requestTracker.getUrl(), requestTracker.getResponseClass());
        }

        return restTemplate.getForEntity(requestTracker.getUrl(), requestTracker.getResponseClass());
    }

    public ResilienceRestTemplate cache(Duration duration) {
        requestTracker.setCacheEnable(true);
        requestTracker.setCacheDuration(duration);
        return this;
    }

}
