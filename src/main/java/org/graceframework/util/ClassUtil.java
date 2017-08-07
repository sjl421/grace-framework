package org.graceframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

/**
 * Created by Tony Liu on 2017/7/31.
 */
public class ClassUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    private static AutoCleanCache<String, Class<?>> classCache = new AutoCleanCache<>();
    /**
     * 获取当前线程的类加载器
     */
    public static ClassLoader getContextClassLoader() {

        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {

        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtil.class.getClassLoader();
            if (null == classLoader) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }
        return classLoader;

    }

    public static Class<?> loadClass(String className) {
        Class<?> cls;
        try {
            cls = Class.forName(className, true, getClassLoader());
        } catch (ClassNotFoundException e) {
            logger.error("加载类出错！", e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 加载类
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        cls = classCache.get(className);
        if (cls != null) {
            return cls;
        }
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            logger.error("加载类出错！", e);
            throw new RuntimeException(e);
        }
        return classCache.put(className,cls);
    }

    /**
     * 判断是否为抽象类
     * @param clazz 类
     * @return 是否为抽象类
     */
    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * 判断是否为接口
     * @param clazz
     * @return 是否为接口
     */
    public static boolean isInterface(Class<?> clazz) {
        return Modifier.isInterface(clazz.getModifiers());
    }

    /**
     * 判断这个类既不是接口也不是抽象类
     * @param clazz 类
     * @return 是否为正常的类
     */
    public static boolean isNormalClass(Class<?> clazz) {

        return !isInterface(clazz) && !isAbstract(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        T instance;
        try {
            Class<?> commandClass = ClassUtil.loadClass(className);
            instance = (T) commandClass.newInstance();
        } catch (Exception e) {
            logger.error("创建实例出错！", e);
            throw new RuntimeException(e);
        }
        return instance;
    }
}
