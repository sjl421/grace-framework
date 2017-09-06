package org.graceframework.mvc.core;


import org.graceframework.InstanceFactory;
import org.graceframework.beans.BeanFactory;
import org.graceframework.beans.Filter;
import org.graceframework.beans.annotation.Controller;
import org.graceframework.beans.annotation.Interceptor;
import org.graceframework.mvc.HandlerInterceptor;
import org.graceframework.mvc.annotation.GET;
import org.graceframework.mvc.annotation.POST;
import org.graceframework.mvc.error.InitialHandlerMappingError;
import org.graceframework.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Tony Liu on 2017/8/4.
 * Servlet 初始化时加载
 */
public class InitHandlerMapping {

    private static final Logger logger = LoggerFactory.getLogger(InitHandlerMapping.class);

    /**
     * 映射的地址是完整的路径
     */
    private static final Map<Requester,HandlerInterceptorChain> fullPathHandlerMapping = new HashMap<>();
    /**
     * 映射的地址带通配符
     */
    private static final Map<Requester,HandlerInterceptorChain> wildcardHandlerMapping = new HashMap<>();

    static {
        //把拦截器和映射关系封装到handlerMapping中
        BeanFactory beanFactory = InstanceFactory.getBeanFactory();
        List<Object> controllerBeans = getControllerBeans(beanFactory);
        List<Object> interceptorBeans = getInterceptorBeansAndSort(beanFactory);
        for (Object controllerBean : controllerBeans) {
            Class<?> controllerClazz = controllerBean.getClass();
            Controller controllerAnnotation = controllerClazz.getAnnotation(Controller.class);
            String controllerPath = StringUtil.doPath(controllerAnnotation.path());
            Method[] controllerMethods = controllerClazz.getDeclaredMethods();
            if (ArrayUtil.isNotEmpty(controllerMethods)) {
                for (Method method : controllerMethods) {
                    Requester requester = createRequester(method,controllerPath);
                    if (requester != null) {

                        if (logger.isDebugEnabled()) {
                            logger.debug("控制器处理器映射开始建立 {} -- {}", requester.getPath(), method);
                        }
                        //验证是否用重复的handle
                        String requestPath = requester.getPath();
                        HandlerInterceptorChain handlerInterceptorChain = createHandlerInterceptorChain(controllerBean, method, requester, interceptorBeans);
                        if (requestPath.contains("*") || requestPath.contains("**")) {
                            wildcardHandlerMapping.put(requester,handlerInterceptorChain);
                        } else {
                            //验证全路径匹配是否用重复的handle
                            HandlerInterceptorChain checkHandler = fullPathHandlerMapping.get(requester);
                            if (checkHandler != null) {
                                throw new InitialHandlerMappingError(
                                        "出现重复的映射关系 " + checkHandler.getControllerBean().getClass() + " " + controllerBean.getClass()+" "+requester.getPath());
                            }
                            fullPathHandlerMapping.put(requester,handlerInterceptorChain);
                        }

                    }
                }
            }
        }
        //检查通配符地址和全路径地址是否有重复
        checkFullAndWildcardHandlerMapping();
    }

    private static void checkFullAndWildcardHandlerMapping() {

        if (MapUtil.isEmpty(wildcardHandlerMapping)) {

            return;
        }

        Set<Map.Entry<Requester, HandlerInterceptorChain>> fullEntries = fullPathHandlerMapping.entrySet();
        for (Map.Entry<Requester, HandlerInterceptorChain> fullEntry: fullEntries) {
            String fullPath = fullEntry.getKey().getPath();
            Set<Map.Entry<Requester, HandlerInterceptorChain>> wildcardEntries = wildcardHandlerMapping.entrySet();
            for (Map.Entry<Requester, HandlerInterceptorChain> wildcardEntry : wildcardEntries) {
                String wildcardPath = wildcardEntry.getKey().getPath();
                if (StringUtil.matcher(fullPath,wildcardPath)) {
                    Object fullControllerBean = fullEntry.getValue().getControllerBean();
                    Object wildcardControllerBean = wildcardEntry.getValue().getControllerBean();
                    throw new InitialHandlerMappingError(
                            "出现重复的映射关系 " + fullControllerBean.getClass() + " " +fullPath +
                                    " "+ wildcardControllerBean.getClass()+ " " + wildcardPath);
                }
            }
        }

    }

    /**
     * 创建处理器
     * @param controllerBean 控制器实例
     * @param method 控制器实例下的方法
     * @param requester 封装好的请求方式和路径
     * @param interceptorBeans 拦截器
     * @return 处理器
     */
    private static HandlerInterceptorChain createHandlerInterceptorChain(Object controllerBean, Method method, Requester requester, List<Object> interceptorBeans) {

        String path = requester.getPath();
        List<HandlerInterceptor> list = null;
        //封装拦截器
        if (CollectionUtil.isNotEmpty(interceptorBeans)) {
            list = new ArrayList<>();
            for (Object interceptorBean : interceptorBeans) {
                if (interceptorBean instanceof HandlerInterceptor) {
                    Interceptor interceptor = interceptorBean.getClass().getAnnotation(Interceptor.class);
                    String interceptorPath = interceptor.path();
                    if (StringUtil.EMPTY.equals(interceptorPath)) {
                        list.add((HandlerInterceptor)interceptorBean);
                    } else if (StringUtil.matcher(path,interceptorPath)) {
                        list.add((HandlerInterceptor)interceptorBean);
                    }
                }
            }
        }

        return new HandlerInterceptorChain(controllerBean, method, list);
    }

    /**
     * 封装请求方式和路径
     * @param method 请求的方式
     * @param controllerPath 请求路径
     * @return 封装好的请求方式和路径
     */
    private static Requester createRequester(Method method,String controllerPath) {

        String requestMethod;
        StringBuilder requestPath = new StringBuilder(controllerPath);
        if (method.isAnnotationPresent(GET.class)) {
            requestMethod = RequestUtil.METHOD_GET;
            GET getAnnotation = method.getAnnotation(GET.class);
            requestPath.append(StringUtil.doPath(getAnnotation.path()));
        } else if (method.isAnnotationPresent(POST.class)) {
            requestMethod = RequestUtil.METHOD_POST;
            POST postAnnotation = method.getAnnotation(POST.class);
            requestPath.append(StringUtil.doPath(postAnnotation.path()));
        } else {
            return null;
        }

        return new Requester(requestMethod,requestPath.toString());

    }

    /**
     * 从BeanFactory容器中获取所有有Controller注解的bean
     * @param beanFactory 容器
     * @return controllerBeans
     */
    private static List<Object> getControllerBeans(BeanFactory beanFactory) {

        return beanFactory.getBeanByYouWant(new Filter<Class<?>>() {
            @Override
            public boolean accept(Class<?> clazz) {
                return clazz.isAnnotationPresent(Controller.class) && ClassUtil.isNormalClass(clazz);
            }
        });

    }

    /**
     * 从BeanFactory容器中获取所有有Interceptor注解的bean
     * 并根据注解上的order属性对list进行排序
     * @param beanFactory 容器
     * @return interceptorBeans
     */
    private static List<Object> getInterceptorBeansAndSort(BeanFactory beanFactory) {

        List<Object> list = beanFactory.getBeanByYouWant(new Filter<Class<?>>() {
            @Override
            public boolean accept(Class<?> clazz) {
                return clazz.isAnnotationPresent(Interceptor.class);
            }
        });

        Collections.sort(list, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                Class<?> clazz1 = o1.getClass();
                Interceptor interceptor1 = clazz1.getAnnotation(Interceptor.class);
                Integer order1 = interceptor1.order();
                Class<?> clazz2 = o2.getClass();
                Interceptor interceptor2 = clazz2.getAnnotation(Interceptor.class);
                Integer order2 = interceptor2.order();
                if (order1 ==0 && order2 == 0) {
                    return clazz1.hashCode() - clazz2.hashCode();
                }
                return order1.compareTo(order2);
            }
        });
        return list;

    }

    /**
     * 获取处理器
     * 匹配逻辑：
     * 1，先全路径匹配
     * 2，全路径没找到 用通配符匹配
     * @param request 请求封装的对象
     * @return 处理器
     */
    static HandlerInterceptorChain getHandler(Requester request){

        HandlerInterceptorChain handlerInterceptorChain;

        if ((handlerInterceptorChain = fullPathHandlerMapping.get(request)) == null) {
            if (MapUtil.isNotEmpty(wildcardHandlerMapping)) {
                String requestPath = request.getPath();
                Set<Map.Entry<Requester, HandlerInterceptorChain>> entries = wildcardHandlerMapping.entrySet();
                for (Map.Entry<Requester, HandlerInterceptorChain> entry : entries) {
                    Requester key = entry.getKey();
                    if (StringUtil.matcher(requestPath,key.getMethod())) {
                        handlerInterceptorChain = entry.getValue();
                        break;
                    }
                }
            }
        }

        return handlerInterceptorChain;

    }

}
