package org.graceframework.mvc.web;

import org.graceframework.beans.core.DependencyInjection;
import org.graceframework.beans.core.InitBeanFactory;
import org.graceframework.mvc.core.InitHandlerMapping;
import org.graceframework.util.ClassUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by Tony Liu on 2017/8/7.
 * 启动web框架 Servlet监听器
 */
@WebListener
public class ContextListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ClassUtil.loadClass(InitBeanFactory.class.getName());
        ClassUtil.loadClass(DependencyInjection.class.getName());
        ClassUtil.loadClass(InitHandlerMapping.class.getName());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
