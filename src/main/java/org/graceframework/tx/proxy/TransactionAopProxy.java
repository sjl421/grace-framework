package org.graceframework.tx.proxy;

import org.graceframework.aop.proxy.AopProxyChain;
import org.graceframework.aop.proxy.AopProxy;
import org.graceframework.mybatis.SqlSessionUtil;
import org.graceframework.tx.annotation.Isolation;
import org.graceframework.tx.annotation.Transactional;

import java.lang.reflect.Method;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class TransactionAopProxy implements AopProxy {

    //处理service方法嵌套
    private static final ThreadLocal<Short> serviceMethodNum = new ThreadLocal<Short>() {
        @Override
        protected Short initialValue() {
            return 0;
        }
    };

    private static void increment() {
        short flag = serviceMethodNum.get();
        serviceMethodNum.set(++flag);
    }

    private static void decrement() {
        short flag = serviceMethodNum.get();
        serviceMethodNum.set(--flag);
    }

    @Override
    public Object doProxy(AopProxyChain chain) throws Throwable {

        Method method = chain.getTargetMethod();
        boolean annotationPresent = method.isAnnotationPresent(Transactional.class);
        boolean autoCommit = true;
        boolean readOnly = true;
        Isolation isolationLevel = Isolation.DEFAULT;
        if (annotationPresent) {
            Transactional transactional = method.getAnnotation(Transactional.class);
            autoCommit = false;
            readOnly = transactional.readOnly();
            isolationLevel = transactional.isolation();
        }
        Object result;
        try {
            //开启事务
            if (serviceMethodNum.get() == 0) {
                SqlSessionUtil.openSqlSession(isolationLevel.value(),autoCommit,readOnly);
            }
            increment();

            //执行service 方法
            result = chain.doProceed();

            decrement();
            if (serviceMethodNum.get() == 0) {
                SqlSessionUtil.commit();
            }
        } catch (Exception e) {
            //抛异常 立即回滚
            serviceMethodNum.set((short)0);
            SqlSessionUtil.rollback();
            throw e;
        } finally {
            if (serviceMethodNum.get() == 0) {
                serviceMethodNum.remove();
                SqlSessionUtil.close();
            }
        }

        return result;
    }
}
