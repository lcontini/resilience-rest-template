package br.com.netodevel.resiliencert.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheScheduler {

    private static Logger log = LoggerFactory.getLogger(CacheScheduler.class);

    private CacheManager cacheManager;

    public CacheScheduler() {
    }

    public CacheScheduler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void cronJob(CacheObject cacheObject) {
        log.info("cache started for the key: {}", cacheObject.getKey());
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(cacheObject.getTimeToLive().getSeconds() * 1000);
                } catch (InterruptedException e) {
                    log.error("cache error: {}", e.getMessage());
                }
                break;
            }

            log.info("cache finalized for the key: {}", cacheObject.getKey());
            cacheManager.removeObject(cacheObject.getKey());
        });

        t.setDaemon(true);
        t.start();
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
