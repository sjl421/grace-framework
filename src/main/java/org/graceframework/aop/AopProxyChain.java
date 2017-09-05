package org.graceframework.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class AopProxyChain {

    private List<Proxy> proxyList = new ArrayList<>();
    private int currentProxyIndex = 0;

    private Object targetObj;
    private Method targetMethod;
    private Object[] args;
    private MethodProxy methodProxy;
    private Object methodResult;

    public AopProxyChain(List<Proxy> proxyList, Object targetObj, Method targetMethod, Object[] args, MethodProxy methodProxy) {
        this.proxyList = proxyList;
        this.targetObj = targetObj;
        this.targetMethod = targetMethod;
        this.args = args;
        this.methodProxy = methodProxy;
    }

    public Object getMethodResult() {
        return methodResult;
    }

    public Object getTargetObj() {
        return targetObj;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getArgs() {
        return args;
    }

    public MethodProxy getMethodProxy() {
        return methodProxy;
    }

    public void doProceed() throws Throwable {

        if (currentProxyIndex < proxyList.size()) {
            methodResult = proxyList.get(currentProxyIndex++).doProxy(this);
        } else {
            try {
                methodResult = methodProxy.invokeSuper(targetObj, args);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

}
