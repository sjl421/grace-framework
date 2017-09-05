package org.graceframework.aop.proxy;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public interface AopProxy {

    Object doProxy(AopProxyChain chain) throws Throwable;
}
