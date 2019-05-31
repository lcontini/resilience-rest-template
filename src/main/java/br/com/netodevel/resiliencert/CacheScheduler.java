package br.com.netodevel.resiliencert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheScheduler {

    private static Logger log = LoggerFactory.getLogger(ProxyRestTemplate.class);

    private final CacheManager cacheManager;

    public CacheScheduler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void cronJob(CacheObject cacheObject) {
        log.info("cron job started to key: {}", cacheObject.getKey());
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(cacheObject.getTimeToLive().getSeconds());
                } catch (InterruptedException e) {
                    log.error("cronJob error: {}", e.getMessage());
                }
                cacheManager.removeObject(cacheObject.getKey());
                break;
            }
        });

        t.setDaemon(true);
        t.start();
    }

}
