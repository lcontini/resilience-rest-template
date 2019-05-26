package br.com.netodevel.resiliencert;


import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;

public class ResilienceRestTemplateAutoConfigurationTest {

    @Test
    public void given_rest_template_should_return_bean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(RestTemplate.class);
        context.register(ResilienceRestTemplateAutoConfiguration.class);
        context.refresh();

        final ResilienceRestTemplate resilienceRestTemplate = context.getBean(ResilienceRestTemplate.class);
        assertNotNull(resilienceRestTemplate);
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void given_no_rest_template_should_return_null() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ResilienceRestTemplateAutoConfiguration.class);
        context.refresh();

        final ResilienceRestTemplate resilienceRestTemplate = context.getBean(ResilienceRestTemplate.class);
    }

}