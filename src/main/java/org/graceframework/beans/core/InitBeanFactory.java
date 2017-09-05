package org.graceframework.beans.core;


import org.graceframework.beans.annotation.Bean;
import org.graceframework.beans.annotation.Controller;
import org.graceframework.beans.annotation.Interceptor;
import org.graceframework.beans.annotation.Service;
import org.graceframework.beans.error.InitialContextError;
import org.graceframework.beans.scanner.ClassScannerEntrance;
import org.graceframework.util.ClassUtil;
import org.graceframework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tong on 2017/8/1.
 */
public abstract class InitBeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(InitBeanFactory.class);

    private static final Map<Class<?>,Object> beanContainerMap = new HashMap<>();
    private static final Map<String,Class<?>> aliasBeanMap = new HashMap<>();

    static {

        Set<Class<?>> classSet = ClassScannerEntrance.getInstance().scanAllPackage();
        try {
            for (Class<?> clazz : classSet) {
                if (needInstance(clazz)){

                    if (logger.isWarnEnabled()) {
                        logger.warn("正在实例化对象 {}...", clazz);
                    }
                    if (!ClassUtil.isNormalClass(clazz)) {
                        throw new InitialContextError("这个类不能被实例化  " + clazz);
                    }

                    Object instance = clazz.newInstance();
                    beanContainerMap.put(clazz, instance);
                    String alias = getAlias(clazz);
                    if (StringUtil.isNotBlank(alias)) {
                        if (!aliasBeanMap.containsKey(alias)) {
                            aliasBeanMap.put(alias, clazz);
                        } else {
                            throw new InitialContextError(
                                    "类实例化失败,不允许相同别名的类实例化  " + aliasBeanMap.get(alias) + "  " +clazz
                            );
                        }
                    }

                }
            }
        } catch (Exception e) {
            throw new InitialContextError("类实例化失败...", e);
        }



    }

    private static boolean needInstance(Class<?> clazz) {

        return clazz != null && (clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Bean.class) ||
                clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Interceptor.class));

    }

    private static String getAlias(Class<?> clazz) {

        if (clazz != null && clazz.isAnnotationPresent(Service.class)) {
            Service service = clazz.getAnnotation(Service.class);
            String value;
            if (StringUtil.isNotBlank(value = service.value())) {
                return value;
            }
        }
        return "";
    }

    static Map<Class<?>,Object> getBeanContainerMap() {

        return beanContainerMap;
    }

    static Map<String,Class<?>> getAliasBeanMap() {

        return aliasBeanMap;
    }
}
