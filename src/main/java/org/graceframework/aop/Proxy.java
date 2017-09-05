package org.graceframework.aop;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public interface Proxy {

    Object doProxy(AopProxyChain chain) throws Throwable;
}
