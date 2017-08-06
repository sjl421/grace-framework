package org.graceframework.beans;


import org.graceframework.beans.exception.BeansException;

import java.util.List;
import java.util.Map;

/**
 * Created by Tong on 2017/8/1.
 */
public interface BeanFactory {

    /**
     * 获取bean容器
     * @return 实例化后的容器
     */
    Map<Class<?>,Object> getBeanContainerMap();

    /**
     * 获取别名与类的映射
     * @return 别名与类的映射关系
     */
    Map<String,Class<?>> getAliasBeanMap();

    /**
     * 根据类获取类的实例
     * @param clazz 类
     * @param <T> 类型
     * @return 返回的实例
     * @throws BeansException 自定义异常 bean为null时抛出
     */
    <T> T getBean(Class<T> clazz) throws BeansException;

    /**
     * 根据别名获取类的实例
     * @param alias 别名
     * @return 返回的实例
     * @throws BeansException 自定义异常 bean为null时抛出
     */
    Object getBean(String alias)  throws BeansException;

    /**
     * 获取过滤器过滤后的类集合
     * @return 实现类
     */
    List<Object> getBeanByYouWant(Filter<Class<?>> classFilter);


}
