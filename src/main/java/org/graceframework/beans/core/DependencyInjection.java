package org.graceframework.beans.core;



import org.graceframework.InstanceFactory;
import org.graceframework.beans.BeanFactory;
import org.graceframework.beans.Filter;
import org.graceframework.beans.annotation.Inject;
import org.graceframework.beans.error.DependencyInjectionError;
import org.graceframework.beans.exception.BeansException;
import org.graceframework.util.ArrayUtil;
import org.graceframework.util.ClassUtil;
import org.graceframework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tony Liu on 2017/8/2.
 * 依赖注入处理类
 */
public class DependencyInjection {

    private static final Logger logger = LoggerFactory.getLogger(DependencyInjection.class);

    static {
        try {
            BeanFactory beanFactory = InstanceFactory.getBeanFactory();
            Map<Class<?>, Object> beanContainerMap = beanFactory.getBeanContainerMap();
            Set<Map.Entry<Class<?>, Object>> entries = beanContainerMap.entrySet();
            for (Map.Entry<Class<?>, Object> beanEntry : entries) {

                Class<?> clazz = beanEntry.getKey();
                Object instance = beanEntry.getValue();
                Field[] fields = clazz.getDeclaredFields();


                if (ArrayUtil.isNotEmpty(fields)) {

                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Inject.class)) {
                            if (logger.isWarnEnabled()) {
                                logger.warn("开始对实例 {} 进行依赖注入 {} ...", instance.getClass(), field.getType());
                            }
                            field.setAccessible(true);
                            Inject inject = field.getAnnotation(Inject.class);
                            String alias = inject.value();
                            if (StringUtil.isNotBlank(alias)) {
                                Object objInject;
                                try {
                                    objInject = beanFactory.getBean(alias);
                                } catch (BeansException e) {
                                    throw new DependencyInjectionError(clazz + " 别名依赖注入失败...",e);
                                }
                                field.set(instance,objInject);
                            } else {
                                Object objInject;
                                try {
                                    objInject = getRealInstance(field.getType(), beanFactory);
                                } catch (BeansException e) {
                                    throw new DependencyInjectionError(clazz + " class依赖注入失败...",e);
                                }
                                field.set(instance,objInject);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new DependencyInjectionError("依赖注入失败...",e);
        }
    }



    /**
     * 获取需要注入的实例
     * @param fieldClass 可能为接口或者抽象类或者正常的类
     * @param beanFactory bean工厂
     * @return 实例
     */
    private static Object getRealInstance(final Class<?> fieldClass, BeanFactory beanFactory) {

        if (!ClassUtil.isNormalClass(fieldClass)) {

            List<Object> list = beanFactory.getBeanByYouWant(new Filter<Class<?>>() {
                @Override
                public boolean accept(Class<?> clazz) {
                    return fieldClass.isAssignableFrom(clazz);
                }
            });
            int size = list.size();
            if (size == 0) {
                throw new DependencyInjectionError("注入失败..." + fieldClass + " 无实现类！");
            }
            if (size > 1) {
                throw new DependencyInjectionError("注入失败..." + fieldClass + "的实现类无法选择 " + list );
            }

            return list.get(0);
        }

        return beanFactory.getBean(fieldClass);
    }


}
