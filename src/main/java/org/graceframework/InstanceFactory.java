package org.graceframework;

import org.graceframework.beans.BeanFactory;
import org.graceframework.beans.core.ApplicationContext;
import org.graceframework.mvc.HandlerMapping;
import org.graceframework.mvc.core.DefaultHandlerMapping;
import org.graceframework.util.ClassUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tony Liu on 2017/8/7.
 */
public class InstanceFactory {

    private static final Map<String,Object> cache = new ConcurrentHashMap<>();

    private static final String APPLICATION_CONTEXT_INSTANCE = "application_context_instance";

    private static final String HANDLER_MAPPING_INSTANCE = "handler_mapping_instance";


    public static BeanFactory getBeanFactory() {

        return getInstance(APPLICATION_CONTEXT_INSTANCE,ApplicationContext.class);
    }

    public static HandlerMapping getHandlerMapping() {

        return getInstance(HANDLER_MAPPING_INSTANCE,DefaultHandlerMapping.class);
    }

    private static <T> T getInstance(String key, Class<T> clazz) {

        Object obj;
        if ((obj = cache.get(key)) != null) {
            return (T)obj;
        }

        obj = ClassUtil.newInstance(clazz.getName());
        cache.put(key, obj);
        return (T) obj;

    }
}
