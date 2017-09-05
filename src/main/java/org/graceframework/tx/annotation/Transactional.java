package org.graceframework.tx.annotation;

import java.lang.annotation.*;

/**
 * Created by Tony Liu on 2017/9/5.
 * 事务注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {

    /**
     * 隔离级别
     */
    Isolation isolation() default Isolation.DEFAULT;

    /**
     * 是否是只读事务
     */
    boolean readOnly() default false;
}
