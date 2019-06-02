package br.com.netodevel.resiliencert.retry;

import br.com.netodevel.resiliencert.RequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProxyRestTemplate implements InvocationHandler {

    private static Logger log = LoggerFactory.getLogger(ProxyRestTemplate.class);

    private Object target;
    private Integer numberOfRetry = 0;
    private List<RequestMapper> requestMappers = new ArrayList<>();
    private List<RequestMapper> requestCompletedWithErros = new ArrayList<>();

    public ProxyRestTemplate(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return invokeMethod(method, args);
    }

    private Object invokeMethod(Method method, Object[] args) {
        RequestMapper requestMapper = new RequestMapper(method, args, false, numberOfRetry);
        requestMappers.add(requestMapper);

        Object object;
        object = invokeRequest(method, args, requestMapper);

        for (RequestMapper rm : requestMappers) {
            if (!rm.getCompleted()) {
                for (int i = rm.getCountRetry(); i >= 0; i--) {
                    if (i != rm.getCountRetry()) log.info("Number of attempts: {}", i + 1);
                    object = invokeRequest(rm.getMethod(), rm.getArgs(), rm);
                    if (object != null) return object;
                }

                log.info("Retry expired request completed with error");
                requestCompletedWithErros.add(rm);
            }
        }
        return object;
    }

    private Object invokeRequest(Method method, Object[] args, RequestMapper requestMapper) {
        try {
            Object object = method.invoke(target, args);
            if (object != null) {
                requestMappers.remove(requestMapper);
                return object;
            }
        } catch (Exception e) {
            log.info("error: {}");
            return null;
        }
        return null;
    }

}
