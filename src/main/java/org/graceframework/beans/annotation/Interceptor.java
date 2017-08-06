package org.graceframework.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Tony Liu on 2017/8/4.
 * 拦截器层
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {

    /**
     * 需要过滤的请求路径
     * @return 请求路径表达式
     */
    String path() default "";

    /**
     * 拦截器执行顺序
     * @return
     */
    int order() default 0;
}
