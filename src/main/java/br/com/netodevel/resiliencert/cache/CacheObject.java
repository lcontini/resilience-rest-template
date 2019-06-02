package br.com.netodevel.resiliencert.cache;

import java.time.Duration;

public class CacheObject {

    private String key;
    private Duration timeToLive;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Duration getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }
}
