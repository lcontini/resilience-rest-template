package br.com.netodevel.resiliencert;

import br.com.netodevel.resiliencert.cache.CacheManager;
import br.com.netodevel.resiliencert.cache.CacheObject;
import br.com.netodevel.resiliencert.cache.CacheScheduler;
import org.junit.Test;

import java.time.Duration;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

public class CacheSchedulerTest {

    @Test
    public void when_cache_expired_should_return_null() throws InterruptedException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.insertCache("my_Key", "my_value");

        CacheObject cacheObject = new CacheObject();
        cacheObject.setKey("my_Key");
        cacheObject.setTimeToLive(Duration.ofSeconds(3));

        CacheScheduler cacheScheduler = new CacheScheduler(cacheManager);
        cacheScheduler.cronJob(cacheObject);

        Thread.sleep(3100);

        assertNull(cacheManager.getCacheValue("my_Key"));
    }

    @Test
    public void when_cache_not_expired_should_return_value() throws InterruptedException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.insertCache("my_Key", "my_value");

        CacheObject cacheObject = new CacheObject();
        cacheObject.setKey("my_Key");
        cacheObject.setTimeToLive(Duration.ofSeconds(3));

        CacheScheduler cacheScheduler = new CacheScheduler(cacheManager);
        cacheScheduler.cronJob(cacheObject);

        Thread.sleep(2000);

        assertNotNull(cacheManager.getCacheValue("my_Key"));
    }

}