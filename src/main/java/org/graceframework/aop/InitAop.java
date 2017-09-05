package org.graceframework.aop;

import org.apache.commons.collections.CollectionUtils;
import org.graceframework.InstanceFactory;
import org.graceframework.aop.proxy.AopProxy;
import org.graceframework.beans.BeanFactory;
import org.graceframework.beans.Filter;
import org.graceframework.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class InitAop {

    private static final Logger logger = LoggerFactory.getLogger(InitAop.class);

    static {
        BeanFactory beanFactory = InstanceFactory.getBeanFactory();
        //aop切面类必须有Aspect注解 和实现AopProxy接口
        List<Object> aopInstanceList = beanFactory.getBeanByYouWant(new Filter<Class<?>>() {
            @Override
            public boolean accept(Class<?> clazz) {
                return AopProxy.class.isAssignableFrom(clazz) && ClassUtil.isNormalClass(clazz);
            }
        });
        if (CollectionUtils.isNotEmpty(aopInstanceList)) {
            for (Object aopInstance : aopInstanceList) {
                
            }
        }

    }
}
