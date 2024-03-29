package br.com.netodevel.resiliencert;

import br.com.netodevel.resiliencert.cache.CacheManager;
import br.com.netodevel.resiliencert.cache.CacheScheduler;
import br.com.netodevel.resiliencert.retry.ProxyRestTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;

@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class ResilienceRestTemplateAutoConfiguration {

    @Bean
    @ConditionalOnBean(RestTemplate.class)
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnBean(RestTemplate.class)
    public ResilienceRestTemplate resilienceRestTemplate() {
        RestOperations restOperations = (RestOperations) Proxy.newProxyInstance(
                ResilienceRestTemplateAutoConfiguration.class.getClassLoader(),
                new Class[]{RestOperations.class},
                new ProxyRestTemplate(restTemplate()));

        ResilienceRestTemplate resilienceRestTemplate = new ResilienceRestTemplate(restTemplate(), restOperations, new CacheManager(), new CacheScheduler());
        resilienceRestTemplate.configureJacksonConvert();

        return resilienceRestTemplate;
    }

}
