package org.graceframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by Tony Liu on 2017/7/31.
 */
public class ClassUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    private static AutoCleanCache<String, Class<?>> classCache = new AutoCleanCache<>();

    private static Class[] types = {Integer.class,
            Double.class,
            Float.class,
            Long.class,
            Short.class,
            Byte.class,
            Boolean.class,
            Character.class,
            String.class,
            int.class,double.class,long.class,short.class,byte.class,boolean.class,char.class,float.class};
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
     * 判断这个类是不是正常的类 也不是包装类型
     * @param clazz 类
     * @return 是否为正常的类
     */
    public static boolean isNormalClass(Class<?> clazz) {
        return null != clazz
                && !isInterface(clazz)
                && !isAbstract(clazz)
                && !clazz.isEnum()
                && !clazz.isArray()
                && !clazz.isAnnotation()
                && !clazz.isSynthetic()
                && !clazz.isPrimitive();
    }

    /**
     * 判断一个类是基本类型或者包装类型
     */
    public static boolean isTypeOrPackageype(Class<?> clazz) {
        return ArrayUtil.contains(types,clazz);
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

    public static Object invoke(Object bean, Method method,Object... params){

        Object obj;
        try {
            method.setAccessible(true);
            if (ArrayUtil.isEmpty(params)) {
                obj = method.invoke(bean);
            } else {
                obj = method.invoke(bean, params);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return obj;
    }


    /**
     * 是否为 int 类型（包括 Integer 类型）
     */
    public static boolean isInt(Class<?> type) {
        return type.equals(int.class) || type.equals(Integer.class);
    }

    /**
     * 是否为 long 类型（包括 Long 类型）
     */
    public static boolean isLong(Class<?> type) {
        return type.equals(long.class) || type.equals(Long.class);
    }

    /**
     * 是否为 double 类型（包括 Double 类型）
     */
    public static boolean isDouble(Class<?> type) {
        return type.equals(double.class) || type.equals(Double.class);
    }

    /**
     * 是否为 String 类型
     */
    public static boolean isString(Class<?> type) {
        return type.equals(String.class);
    }

    /**
     * 是否为 Float 类型
     */
    public static boolean isFloat(Class<?> type) {

        return type.equals(float.class) || type.equals(Float.class);
    }

    /**
     * 是否为bool类型
     */
    public static boolean isBoolean(Class<?> type) {

        return type.equals(boolean.class) || type.equals(Boolean.class);
    }
}
