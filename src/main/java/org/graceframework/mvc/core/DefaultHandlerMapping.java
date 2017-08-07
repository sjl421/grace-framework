package org.graceframework.mvc.core;

import org.graceframework.mvc.HandlerMapping;
import org.graceframework.util.StringUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Tong on 2017/8/5.
 * 处理器映射入口
 */
public class DefaultHandlerMapping implements HandlerMapping {


    @Override
    public HandlerInterceptorChain getHandler(HttpServletRequest request) {

        //处理request或取请求路径
        String method = request.getMethod();
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (path.contains(StringUtil.DOT)) {
            path = path.substring(0, path.indexOf(StringUtil.DOT));
        }
        if (StringUtil.isNotBlank(contextPath)) {
            path = path.replace(contextPath,StringUtil.EMPTY);
        }
        return getHandler(new Requester(method,path));
    }

    public HandlerInterceptorChain getHandler(Requester requester) {
        return InitHandlerMapping.getHandler(requester);
    }
}
