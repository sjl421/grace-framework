package org.graceframework.aop;

import org.apache.commons.collections.CollectionUtils;
import org.graceframework.InstanceFactory;
import org.graceframework.aop.proxy.AopProxy;
import org.graceframework.beans.BeanFactory;
import org.graceframework.beans.Filter;
import org.graceframework.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Liu on 2017/9/5.
 */
public class InitAop {

    private static final Logger logger = LoggerFactory.getLogger(InitAop.class);

    static {

        //获取aop切面类和目标类的映射关系
        Map<Object,List<Class<?>>> aopToClsses = getAopToTargetClasses();



    }

    /**
     * aop切面类必须有Aspect注解 和实现AopProxy接口
     */
    private static Map<Object,List<Class<?>>> getAopToTargetClasses() {

        Map<Object,List<Class<?>>> aopToClsses = new HashMap<>();
        BeanFactory beanFactory = InstanceFactory.getBeanFactory();
        List<Object> aopInstanceList = beanFactory.getBeanByYouWant(new Filter<Class<?>>() {
            @Override
            public boolean accept(Class<?> clazz) {
                return AopProxy.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Aspect.class) && ClassUtil.isNormalClass(clazz);
            }
        });
        if (CollectionUtils.isNotEmpty(aopInstanceList)) {
            for (Object aopInstance : aopInstanceList) {
                Aspect annotation = aopInstance.getClass().getAnnotation(Aspect.class);
                aopToClsses.put(aopInstance,getTargetClasses(annotation));
            }
        }
        return aopToClsses;
    }

    /**
     * 根据注解上定义的信息 获取目标类集合
     * @param aspect
     * @return
     */
    private static List<Class<?>> getTargetClasses(Annotation aspect) {
        return null;
    }
}
