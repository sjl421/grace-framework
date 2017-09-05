package org.graceframework.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.graceframework.aop.AopProxyChain;
import org.graceframework.aop.Proxy;
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

    @SuppressWarnings("unchecked")
    public static <T> T createAopProxy(final Class<T> clazz, final List<Proxy> proxyList) {

        return (T) Enhancer.create(clazz, new MethodInterceptor() {

            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

                AopProxyChain aopProxyChain = new AopProxyChain(proxyList, o, method, objects, methodProxy);
                return aopProxyChain.getMethodResult();
            }
        });
    }
}
