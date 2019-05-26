package br.com.netodevel.resiliencert;

import java.lang.reflect.Method;

public class RequestMapper {

    private Method method;
    private Object[] args;
    private Boolean completed;
    private Integer countRetry;

    public RequestMapper(){}

    public RequestMapper(Method method, Object[] args, Boolean completed, Integer countRetry) {
        this.method = method;
        this.args = args;
        this.completed = completed;
        this.countRetry = countRetry;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getCountRetry() {
        return countRetry;
    }

    public void setCountRetry(Integer countRetry) {
        this.countRetry = countRetry;
    }
}
