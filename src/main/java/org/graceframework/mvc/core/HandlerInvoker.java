package org.graceframework.mvc.core;

import org.graceframework.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Tong on 2017/8/7.
 */
public class HandlerInvoker {


    public static void invoke(HttpServletRequest request, HttpServletResponse response, HandlerInterceptorChain handler) {

        Object controllerBean = handler.getControllerBean();
        Method method = handler.getHandler();
        String contentType = request.getContentType();
        Object[] params;
        //当普通表单处理
        if (StringUtil.isBlank(contentType)) {
            params = createParamListNormal(request, handler);
            Object ret = ClassUtil.invoke(controllerBean, method, params);
            if (ret != null) {
                try {
                    // 设置响应头
                    response.setHeader("Content-type", "application/json;charset=UTF-8");
                    response.setCharacterEncoding("utf-8"); // 防止中文乱码
                    // 向响应中写入数据
                    PrintWriter writer = response.getWriter();
                    writer.write(ret.toString());
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    //需要简化 抽离
    private static Object[] createParamListNormal(HttpServletRequest request, HandlerInterceptorChain handler) {

        Object controllerBean = handler.getControllerBean();

        Method method = handler.getHandler();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (ArrayUtil.isNotEmpty(parameterTypes)) {
            Class<?> controllerClazz = controllerBean.getClass();
            Map<Class<?>,String> methodParamNameAndParamClassMap = JavassistUtil.getMethodParamNameAndParamClassMap(controllerClazz, method);
            int paramLength = parameterTypes.length;
            List<Object> paramArr = new ArrayList<>();
            for (int i = 0; i < paramLength; i++) {
                Class<?> parameterType = parameterTypes[i];

                if (ClassUtil.isTypeOrPackageype(parameterType)) {
                    String paramName = methodParamNameAndParamClassMap.get(parameterType);
                    String parameter = request.getParameter(paramName);
                    if (StringUtil.isNotBlank(parameter)) {
                        Object value = WebCastUtil.getValue(parameterType, parameter);
                        if (value != null) {
                            paramArr.add(value);
                        }
                    }
                } else {
                    try {
                        Object obj = parameterType.newInstance();

                        Field[] fields = obj.getClass().getDeclaredFields();
                        int sum = 0;
                        for (Field field : fields) {
                            int mod = field.getModifiers();
                            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                                continue;
                            }

                            String parameter1 = request.getParameter(field.getName());
                            if (StringUtil.isNotBlank(parameter1)) {
                                field.setAccessible(true);
                                Object fieldValue = WebCastUtil.getValue(field.getType(), parameter1);
                                field.set(obj, fieldValue);
                                sum ++;
                            }
                        }
                        if (sum > 0) {
                            paramArr.add(obj);
                        } else {
                            throw new RuntimeException("没有任何参数能匹配这个对象 " + parameterType);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            //检查参数长度
            if (paramArr.size() != paramLength) {
                throw new RuntimeException("传入参数与控制器不匹配...");
            }

            return paramArr.toArray();
        }

        return null;
    }

    private static Map<String,String> getRequestParamMap(HttpServletRequest request) {

        Map<String, String> paramMap = new HashMap<>();
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
