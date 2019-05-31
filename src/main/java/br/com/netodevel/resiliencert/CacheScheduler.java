package br.com.netodevel.resiliencert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheScheduler {

    private static Logger log = LoggerFactory.getLogger(ProxyRestTemplate.class);

    private CacheManager cacheManager;

    public CacheScheduler() {
    }

    public CacheScheduler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void cronJob(CacheObject cacheObject) {
        log.info("cron job started to key: {}", cacheObject.getKey());
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(cacheObject.getTimeToLive().getSeconds() * 1000);
                } catch (InterruptedException e) {
                    log.error("cronJob error: {}", e.getMessage());
                }
                break;
            }

            log.info("cron job finish to key: {}", cacheObject.getKey());
            cacheManager.removeObject(cacheObject.getKey());
        });

        t.setDaemon(true);
        t.start();
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
