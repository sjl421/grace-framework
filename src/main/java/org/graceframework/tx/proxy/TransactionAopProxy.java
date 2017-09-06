package org.graceframework.tx.proxy;

import org.graceframework.aop.proxy.AopProxy;
import org.graceframework.aop.proxy.AopProxyChain;
import org.graceframework.mybatis.SqlSessionUtil;
import org.graceframework.tx.annotation.Isolation;
import org.graceframework.tx.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class TransactionAopProxy implements AopProxy {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAopProxy.class);
    //处理service方法嵌套
    private static final ThreadLocal<Boolean> serviceMethodBool = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Override
    public Object doProxy(AopProxyChain chain) throws Throwable {

        Method method = chain.getTargetMethod();

        boolean flag = serviceMethodBool.get();
        Object result;
        if (!flag) {
            serviceMethodBool.set(true);
            boolean autoCommit = true;
            boolean readOnly = false;
            Isolation isolationLevel = Isolation.DEFAULT;
            if (method.isAnnotationPresent(Transactional.class)) {
                Transactional transactional = method.getAnnotation(Transactional.class);
                autoCommit = false;
                readOnly = transactional.readOnly();
                isolationLevel = transactional.isolation();
            }

            try {
                //开启事务
                if (logger.isDebugEnabled() && !autoCommit) {
                    logger.debug("开启事务 {}", Thread.currentThread().getName());
                }
                SqlSessionUtil.openSqlSession(isolationLevel.value(),autoCommit,readOnly);

                //执行service 方法
                result = chain.doProceed();

                if (logger.isDebugEnabled() && !autoCommit) {
                    logger.debug("提交事务 {}", Thread.currentThread().getName());
                }
                SqlSessionUtil.commit();
            } catch (Exception e) {
                //抛异常 立即回滚
                if (logger.isDebugEnabled() && !autoCommit) {
                    logger.debug("事务回滚 {}", Thread.currentThread().getName());
                }
                SqlSessionUtil.rollback();
                throw e;
            } finally {
                if (logger.isDebugEnabled() && !autoCommit) {
                    logger.debug("关闭连接 {}", Thread.currentThread().getName());
                }
                serviceMethodBool.remove();
                SqlSessionUtil.close();
            }
        } else {
            //执行service 方法
            result = chain.doProceed();
        }

        return result;
    }
}
