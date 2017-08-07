package org.graceframework.mvc.core;

import org.graceframework.util.ArrayUtil;
import org.graceframework.util.JavassistUtil;
import org.graceframework.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tong on 2017/8/7.
 */
public class HandlerInvoker {


    public static void invok(HttpServletRequest request, HttpServletResponse response, HandlerInterceptorChain handler) {

        String contentType = request.getContentType();
        Object[] params;
        //当普通表单处理
        if (StringUtil.isBlank(contentType)) {
            params = createParamListNormal(request, handler);
        }

    }

    private static Object[] createParamListNormal(HttpServletRequest request, HandlerInterceptorChain handler) {

        Class<?> controllerClazz = handler.getControllerBean().getClass();
        Method method = handler.getHandler();
        Map<String, Object> methodParamNameAndParamClassMap = JavassistUtil.getMethodParamNameAndParamClassMap(controllerClazz, method);
        Map<String, Object> requestParamMap = getRequestParamMap(request);
        
        return null;
    }

    private static Map<String,Object> getRequestParamMap(HttpServletRequest request) {

        Map<String, Object> paramMap = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (checkParamName(paramName)) {
                String[] paramValues = request.getParameterValues(paramName);
                if (ArrayUtil.isNotEmpty(paramValues)) {
                    if (paramValues.length == 1) {
                        paramMap.put(paramName, paramValues[0]);
                    } else {
                        StringBuilder paramValue = new StringBuilder("");
                        for (int i = 0; i < paramValues.length; i++) {
                            paramValue.append(paramValues[i]);
                            if (i != paramValues.length - 1) {
                                paramValue.append(StringUtil.SEPARATOR);
                            }
                        }
                        paramMap.put(paramName, paramValue.toString());
                    }
                }
            }
        }

        return paramMap;
    }

    private static boolean checkParamName(String paramName) {
        return !paramName.equals("_"); // 忽略 jQuery 缓存参数
    }


}
