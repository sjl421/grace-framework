package org.graceframework.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Tony Liu on 2017/8/7.
 */
public interface HandlerExceptionResolver {

    void resolveHandlerException(HttpServletRequest request, HttpServletResponse response, Exception e);
}
