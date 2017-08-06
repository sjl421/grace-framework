package org.graceframework.beans;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by Tony Liu on 2017/7/31.
 */
public interface ClassScanner {

    /**
     * 获取指定包下所有类
     */
    Set<Class<?>> scanAllPackage(String packageName);

    /**
     * 获取指定注解的类
     */
    Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass);

    /**
     * 获取指定包名中指定父类或接口的相关类
     */
    Set<Class<?>> scanPackageBySuper(String packageName, Class<?> superClass);

    /**
     * 自己实现过滤器获取你想要的类集合
     */
    Set<Class<?>> scanPackageGetYouWant(String packageName, Filter<Class<?>> classFilter);
}
