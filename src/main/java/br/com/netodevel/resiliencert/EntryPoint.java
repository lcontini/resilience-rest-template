package br.com.netodevel.resiliencert;

import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;

public class EntryPoint {

    private RestOperations restOperationToProxy;
    private RestTemplate restTemplate;

    public RestOperations configureRetryProxy() {
        restOperationToProxy = (RestOperations) Proxy.newProxyInstance(
                ResilienceRestTemplate.class.getClassLoader(),
                new Class[]{RestOperations.class},
                new ProxyRestTemplate(this.restTemplate));
        return restOperationToProxy;
    }
}
