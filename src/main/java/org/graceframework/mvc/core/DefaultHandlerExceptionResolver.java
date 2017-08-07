package org.graceframework.mvc.core;

import org.graceframework.mvc.HandlerExceptionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Tony Liu on 2017/8/7.
 */
public class DefaultHandlerExceptionResolver implements HandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultHandlerExceptionResolver.class);

    public final static String SERVER_ERROR_MSG = "服务器正在维护。";


    @Override
    public void resolveHandlerException(HttpServletRequest request, HttpServletResponse response, Exception e) {

        try {
            response.sendError(500,SERVER_ERROR_MSG+e.getCause().getMessage());
            logger.error("请求异常：", e.getCause());
        } catch (Exception ex) {
            logger.error("DefaultHandlerExceptionResolver 出现异常",ex.getCause());
        }

    }
}
