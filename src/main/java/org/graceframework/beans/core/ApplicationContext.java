package org.graceframework.beans.core;


import org.graceframework.beans.Filter;
import org.graceframework.beans.exception.BeansException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tong on 2017/8/1.
 * 实例化bean的容器
 */
public class ApplicationContext extends AbstractBeanFactory {

    private static ApplicationContext applicationContext = new ApplicationContext();

    private ApplicationContext() {
        DependencyInjection.IocInject(this);
    }

    public static ApplicationContext getInstance() {

        return applicationContext;
    }

    @Override
    public Map<Class<?>,Object> getBeanContainerMap() {

        return beanContainerMap;
    }

    @Override
    public Map<String,Class<?>> getAliasBeanMap() {

        return aliasBeanMap;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException {

        Object obj = beanContainerMap.get(clazz);
        if (obj != null) {
            return (T) obj;
        } else {
            throw new BeansException(clazz + "  没有找到该实例...");
        }

    }

    @Override
    public Object getBean(String alias) throws BeansException {

        Class<?> clazz = aliasBeanMap.get(alias);
        if (clazz != null) {
            return getBean(clazz);
        } else {
            throw new BeansException("根据这个别名:" + alias + "  没有找到对应实例...");
        }
    }

    @Override
    public List<Object> getBeanByYouWant(Filter<Class<?>> classFilter) {

        List<Object> objects = new ArrayList<>();
        Set<Map.Entry<Class<?>, Object>> entries = beanContainerMap.entrySet();

        for (Map.Entry<Class<?>, Object> beanEntry : entries) {

            Class<?> clazz = beanEntry.getKey();
            if (classFilter.accept(clazz)) {
                objects.add(beanEntry.getValue());
            }
        }

        return objects;
    }

}
