package br.com.netodevel.resiliencert;

import java.time.Duration;

public class RequestTracker<T> {

    private Class<T> responseClass;
    private String url;
    private Integer numberOfAttempts;
    private Boolean retryEnable = Boolean.FALSE;
    private Boolean cacheEnable = Boolean.FALSE;
    private Boolean fallbackEnable = Boolean.FALSE;
    private Duration cacheDuration;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(Integer numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public Boolean getRetryEnable() {
        return retryEnable;
    }

    public void setRetryEnable(Boolean retryEnable) {
        this.retryEnable = retryEnable;
    }

    public Boolean getCacheEnable() {
        return cacheEnable;
    }

    public void setCacheEnable(Boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
    }

    public Duration getCacheDuration() {
        return cacheDuration;
    }

    public void setCacheDuration(Duration cacheDuration) {
        this.cacheDuration = cacheDuration;
    }

    public Class<T> getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(Class<T> responseClass) {
        this.responseClass = responseClass;
    }

    public Boolean getFallbackEnable() {
        return fallbackEnable;
    }

    public void setFallbackEnable(Boolean fallbackEnable) {
        this.fallbackEnable = fallbackEnable;
    }
}
