package org.graceframework.mvc.core;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Tong on 2017/8/5.
 */
public class DefaultHandlerMapping extends AbstractHandlerMapping{


    @Override
    public HandlerInterceptorChain getHandler(HttpServletRequest request){

        return null;
    }



}
