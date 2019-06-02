package br.com.netodevel.resiliencert.cache;

import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    private ConcurrentHashMap<String, Object> cacheReponse = new ConcurrentHashMap<>();

    public void insertCache(String key, Object value) {
        this.cacheReponse.put(key, value);
    }

    public Object getCacheValue(String key) {
        return this.cacheReponse.get(key);
    }

    public void removeObject(String key) {
        this.cacheReponse.remove(key);
    }

}
