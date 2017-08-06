package org.graceframework.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Tony Liu on 2017/8/4.
 */
public interface HandlerInterceptor {

    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

    void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

    void test();
}
