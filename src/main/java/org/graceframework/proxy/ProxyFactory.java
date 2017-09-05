package org.graceframework.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.graceframework.aop.proxy.AopProxyChain;
import org.graceframework.aop.proxy.AopProxy;
import org.graceframework.mybatis.MybatisContext;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class ProxyFactory {


    /**
     * mybatis mapper接口实例代理
     * @param clazz mapper接口
     * @return mapper实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T createMapperInstanceProxy(final Class<T> clazz) {

        return (T) Enhancer.create(clazz, new MethodInterceptor() {

            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

                Object instance = MybatisContext.getMapper(clazz);

                return method.invoke(instance,objects);

            }
        });
    }

    /**
     * aop代理
     * @param clazz 目标类
     * @param aopProxyList 代理链
     * @return aop代理
     */
    @SuppressWarnings("unchecked")
    public static <T> T createAopProxy(final Class<T> clazz, final List<AopProxy> aopProxyList) {

        return (T) Enhancer.create(clazz, new MethodInterceptor() {

            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

                return new AopProxyChain(aopProxyList, o, method, objects, methodProxy).doProceed();
            }
        });
    }
}
