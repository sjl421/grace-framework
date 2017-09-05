package org.graceframework.aop.proxy;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class AopProxyChain {

    private List<AopProxy> aopProxyList = new ArrayList<>();
    private int currentProxyIndex = 0;

    private Object targetObj;
    private Method targetMethod;
    private Object[] args;
    private MethodProxy methodProxy;

    public AopProxyChain(List<AopProxy> aopProxyList, Object targetObj, Method targetMethod, Object[] args, MethodProxy methodProxy) {
        this.aopProxyList = aopProxyList;
        this.targetObj = targetObj;
        this.targetMethod = targetMethod;
        this.args = args;
        this.methodProxy = methodProxy;
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

    public Object doProceed() throws Throwable {

        Object methodResult;
        if (currentProxyIndex < aopProxyList.size()) {
            methodResult = aopProxyList.get(currentProxyIndex++).doProxy(this);
        } else {
            try {
                methodResult = methodProxy.invokeSuper(targetObj, args);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return methodResult;
    }

}
