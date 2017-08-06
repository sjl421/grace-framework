package org.graceframework.mvc.core;

import org.graceframework.beans.scanner.DefaultClassScanner;
import org.graceframework.mvc.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Tony Liu on 2017/8/4.
 * 拦截器链
 */
public class HandlerInterceptorChain {

    private static final Logger logger = LoggerFactory.getLogger(DefaultClassScanner.class);

    private List<HandlerInterceptor> interceptors;
    private Method handler;
    private Object controllerBean;
    private int interceptorIndex;

    public HandlerInterceptorChain(Object controllerBean, Method handler, List<HandlerInterceptor> interceptors) {
        interceptorIndex = -1;
        this.controllerBean = controllerBean;
        this.handler = handler;
        this.interceptors = interceptors;
    }

    public List<HandlerInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<HandlerInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public Method getHandler() {
        return handler;
    }

    public void setHandler(Method handler) {
        this.handler = handler;
    }

    public Object getControllerBean() {
        return controllerBean;
    }

    public void setControllerBean(Object controllerBean) {
        this.controllerBean = controllerBean;
    }

    public int getInterceptorIndex() {
        return interceptorIndex;
    }

    public void setInterceptorIndex(int interceptorIndex) {
        this.interceptorIndex = interceptorIndex;
    }

    @Override
    public String toString() {
        return "HandlerInterceptorChain{" +
                "interceptors=" + interceptors +
                ", handler=" + handler +
                ", controllerBean=" + controllerBean.getClass() +
                ", interceptorIndex=" + interceptorIndex +
                '}';
    }
}
