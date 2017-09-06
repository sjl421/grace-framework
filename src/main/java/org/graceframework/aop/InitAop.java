package org.graceframework.aop;

import org.graceframework.InstanceFactory;
import org.graceframework.aop.proxy.AopProxy;
import org.graceframework.beans.BeanFactory;
import org.graceframework.beans.Filter;
import org.graceframework.beans.annotation.Service;
import org.graceframework.beans.scanner.ClassScannerEntrance;
import org.graceframework.proxy.ProxyFactory;
import org.graceframework.tx.proxy.TransactionAopProxy;
import org.graceframework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by Tony Liu on 2017/9/5.
 * 用于封装代理类 切面 事务
 */
public class InitAop {

    private static final Logger logger = LoggerFactory.getLogger(InitAop.class);

    private static final BeanFactory beanFactory = InstanceFactory.getBeanFactory();
    private static final ClassScannerEntrance scanner = ClassScannerEntrance.getInstance();

    static {
        //获取aop切面类和目标类的映射关系
        Map<Object,Set<Class<?>>> aopToClsses = getAopToTargetClasses();
        //获取目标类和aopList的映射关系
        Map<Class<?>,List<AopProxy>> targetToAopList = getTargetClassesToAopList(aopToClsses);
        //封装进BeanFactory中
        Set<Map.Entry<Class<?>, List<AopProxy>>> entries = targetToAopList.entrySet();
        for (Map.Entry<Class<?>, List<AopProxy>> entry : entries) {
            Class<?> clazz = entry.getKey();
            List<AopProxy> aopProxies = entry.getValue();
            beanFactory.setBean(clazz, ProxyFactory.createAopProxy(clazz, aopProxies));
        }
    }

    /**
     * 获取 目标类和aopList的映射关系
     */
    private static Map<Class<?>,List<AopProxy>> getTargetClassesToAopList(Map<Object, Set<Class<?>>> aopToClsses) {

        Map<Class<?>,List<AopProxy>> map = new HashMap<>();
        Set<Map.Entry<Object, Set<Class<?>>>> entries = aopToClsses.entrySet();
        for (Map.Entry<Object, Set<Class<?>>> entry : entries) {
            AopProxy proxy = (AopProxy) entry.getKey();
            Set<Class<?>> targetClasses = entry.getValue();
            for (Class<?> targetClass : targetClasses) {
                List<AopProxy> aopProxies = map.get(targetClass);
                if (aopProxies == null) {
                    aopProxies = new ArrayList<>();
                    aopProxies.add(proxy);
                } else {
                    aopProxies.add(proxy);
                }
                map.put(targetClass,aopProxies);
            }
        }
        return map;
    }

    /**
     * aop切面类必须有Aspect注解 和实现AopProxy接口
     */
    private static Map<Object,Set<Class<?>>> getAopToTargetClasses() {

        //有序集合
        Map<Object,Set<Class<?>>> aopToClasses = new LinkedHashMap<>();

        List<Object> aopInstanceList = getAopInstanceListAndSort();
        for (Object aopInstance : aopInstanceList) {
            Aspect annotation = aopInstance.getClass().getAnnotation(Aspect.class);
            Set<Class<?>> targetClasses = getTargetClasses(annotation);
            if (targetClasses.size() > 0) {
                aopToClasses.put(aopInstance,targetClasses);
            } else {
                logger.warn("{} 切面类，没有发现任何目标类",aopInstance.getClass());
            }

        }
        Set<Class<?>> serviceClasses = getServiceClasses();
        if (serviceClasses.size() > 0) {
            aopToClasses.put(new TransactionAopProxy(),serviceClasses);
        } else {
            logger.warn("{} 切面类，没有发现任何目标类",TransactionAopProxy.class);
        }

        return aopToClasses;
    }

    /**
     * 根据order注解排序
     */
    private static List<Object> getAopInstanceListAndSort() {


        List<Object> aopInstanceList = beanFactory.getBeanByYouWant(new Filter<Class<?>>() {
            @Override
            public boolean accept(Class<?> clazz) {
                return AopProxy.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Aspect.class);
            }
        });
        Collections.sort(aopInstanceList, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                Class<?> clazz1 = o1.getClass();
                Aspect Aspect1 = clazz1.getAnnotation(Aspect.class);
                Integer order1 = Aspect1.order();
                Class<?> clazz2 = o2.getClass();
                Aspect Aspect2 = clazz2.getAnnotation(Aspect.class);
                Integer order2 = Aspect2.order();
                if (order1 == 0 && order2 == 0) {
                    return clazz1.hashCode() - clazz2.hashCode();
                }
                return order1.compareTo(order2);
            }
        });

        return aopInstanceList;
    }

    /**
     * service 加入事务控制
     */
    private static Set<Class<?>> getServiceClasses() {

        return scanner.scanPackageByAnnotation("", Service.class);
    }

    /**
     * 根据注解上定义的信息 获取目标类集合
     */
    private static Set<Class<?>> getTargetClasses(Aspect aspect) {

        Set<Class<?>> set = new HashSet<>();
        String pk = aspect.pk();
        if (StringUtil.isNotBlank(pk)) {
            Set<Class<?>> classSet = scanner.scanPackage(pk);
            set.addAll(classSet);
        }
        Class<?> targetClazz = aspect.aopClass();
        if (targetClazz != null) {
            set.add(targetClazz);
        }
        Class<? extends Annotation> aopAnnotation = aspect.aopAnnotation();
        if (aopAnnotation != null) {
            Set<Class<?>> classSet = ClassScannerEntrance.getInstance().scanPackageByAnnotation(pk, aopAnnotation);
            set.addAll(classSet);
        }
        return set;
    }
}
