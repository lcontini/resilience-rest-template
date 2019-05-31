package br.com.netodevel.resiliencert;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private Map<String, Object> cacheReponse = new HashMap<>();

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
