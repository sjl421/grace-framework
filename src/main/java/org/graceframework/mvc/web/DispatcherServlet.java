package org.graceframework.mvc.web;

import org.graceframework.InstanceFactory;
import org.graceframework.beans.BeanFactory;
import org.graceframework.beans.Filter;
import org.graceframework.mvc.HandlerExceptionResolver;
import org.graceframework.mvc.HandlerMapping;
import org.graceframework.mvc.core.DefaultHandlerExceptionResolver;
import org.graceframework.mvc.core.HandlerInterceptorChain;
import org.graceframework.mvc.core.HandlerInvoker;
import org.graceframework.mvc.core.RequestUtil;
import org.graceframework.mvc.error.InitDispatcherServletError;
import org.graceframework.util.ClassUtil;
import org.graceframework.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Tony Liu on 2017/8/7.
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    private static HandlerMapping handlerMapping;
    private static HandlerExceptionResolver handlerExceptionResolver = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        handlerMapping = InstanceFactory.getHandlerMapping();
        handlerExceptionResolver = getHandlerExceptionResolver();
        super.init(config);
    }



    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding(RequestUtil.CHARACTER_ENCODING);

        if (logger.isDebugEnabled()) {
            logger.debug("接收到 {}请求，请求地址 {}...", request.getMethod(), request.getRequestURI());
        }

        HandlerInterceptorChain handler = handlerMapping.getHandler(request);

        if (handler == null) {
            response.sendError(404,"你访问的地址不存在...");
            logger.error("根据请求 {} 没有找到对应的处理器...", request.getRequestURI());
            return;
        }

        //preHandle...


        try {

            HandlerInvoker.invoke(request, response , handler);

        } catch (Exception e) {
            handlerExceptionResolver.resolveHandlerException(request, response, e);
        }

        //postHandle...
        //super.service(req, resp);
    }


    /**
     * 获取HandlerExceptionResolver的实现类
     * @return HandlerExceptionResolver
     */
    private HandlerExceptionResolver getHandlerExceptionResolver() {

        BeanFactory beanFactory = InstanceFactory.getBeanFactory();
        List<Object> list = beanFactory.getBeanByYouWant(new Filter<Class<?>>() {
            @Override
            public boolean accept(Class<?> clazz) {

                return HandlerExceptionResolver.class.isAssignableFrom(clazz) && ClassUtil.isNormalClass(clazz);
            }
        });

        if (CollectionUtil.isNotEmpty(list)) {
            if (list.size() > 1) {
                throw new InitDispatcherServletError("HandlerExceptionResolver 只允许有一个实现类");
            }
            return (HandlerExceptionResolver)list.get(0);
        } else {
            return new DefaultHandlerExceptionResolver();
        }
    }
}
