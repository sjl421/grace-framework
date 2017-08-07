package org.graceframework.mvc;


import org.graceframework.mvc.core.HandlerInterceptorChain;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Tony Liu on 2017/8/4.
 * 处理器映射器
 */
public interface HandlerMapping {

    /**
     * 返回处理器
     * @param request 请求 根据它匹配处理器
     * @return 返回处理器链
     * @throws Exception
     */
    HandlerInterceptorChain getHandler(HttpServletRequest request);

}
