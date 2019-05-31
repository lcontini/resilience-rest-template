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

    /**
     * Cache Scheduler
     */
    private CacheScheduler cacheScheduler;

    public ResilienceRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResilienceRestTemplate(RestTemplate restTemplate, RestOperations restOperationToProxy, CacheManager cacheManager, CacheScheduler cacheScheduler) {
        this.restTemplate = restTemplate;
        this.restOperationToProxy = restOperationToProxy;
        this.cacheManager = cacheManager;
        this.cacheScheduler = cacheScheduler;
    }

    public ResilienceRestTemplate(RestTemplate restTemplate, RestOperations restOperationToProxy, CacheManager cacheManager) {
        this.restTemplate = restTemplate;
        this.restOperationToProxy = restOperationToProxy;
        this.cacheManager = cacheManager;
    }

    public ResilienceRestTemplate(RestTemplate restTemplate, RestOperations restOperations) {
        this.restTemplate = restTemplate;
        this.restOperationToProxy = restOperations;
    }

    public void configureJacksonConvert() {
        if (this.restTemplate != null) restTemplate.setMessageConverters(MessageConvert.convertJackson());
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

    public <T> ResponseEntity<T> call() throws RestClientException {
        if (requestTracker.getCacheEnable()) {
            createCacheScheduler();

            Object object = cacheManager.getCacheValue(requestTracker.getUrl());
            if (object != null) return (ResponseEntity<T>) object;
        }

        if (requestTracker.getRetryEnable()) {
            ResponseEntity<T> responseEntity = restOperationToProxy.getForEntity(requestTracker.getUrl(), requestTracker.getResponseClass());
            if (responseEntity != null) cacheManager.insertCache(requestTracker.getUrl(), responseEntity.getBody());

            return responseEntity;
        }

        ResponseEntity<T> responseEntity = restTemplate.getForEntity(requestTracker.getUrl(), requestTracker.getResponseClass());
        cacheManager.insertCache(requestTracker.getUrl(), responseEntity.getBody());
        return responseEntity;
    }

    private void createCacheScheduler() {
        if (this.cacheScheduler != null) {
            CacheObject cacheObject = new CacheObject();
            cacheObject.setKey(requestTracker.getUrl());
            cacheObject.setTimeToLive(requestTracker.getCacheDuration());

            cacheScheduler.setCacheManager(this.cacheManager);
            cacheScheduler.cronJob(cacheObject);
        }
    }

    public ResilienceRestTemplate cache(Duration duration) {
        requestTracker.setCacheEnable(true);
        requestTracker.setCacheDuration(duration);
        return this;
    }

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }

    protected CacheManager getCacheManager() {
        return cacheManager;
    }
}
