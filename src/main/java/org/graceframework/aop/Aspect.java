package org.graceframework.aop;

import java.lang.annotation.*;

/**
 * Created by Tony Liu on 2017/9/5.
 * 定义切面类时 没有这个注解的类 不会被实例化
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    /**
     * 该包下的所有类 都需要代理
     */
    String pk() default "";

    /**
     * 需要代理的具体类
     * @return
     */
    Class<?> aopClass();

    /**
     * 定义在需要代理的类上
     */
    Class<? extends Annotation> aopAnnotation();

    /**
     * aop执行顺序
     */
    int order() default 0;
}
