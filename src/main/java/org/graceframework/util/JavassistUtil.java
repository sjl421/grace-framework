package org.graceframework.util;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class JavassistUtil {

    /**
     * 获取方法的参数名称和参数的类型映射
     * @param clazz 控制器类
     * @param method 方法
     * @return 参数名称和参数的类型映射
     */
    public static Map<String,Object> getMethodParamNameAndParamClassMap(Class<?> clazz, Method method) {

        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(JavassistUtil.class);
        pool.insertClassPath(classPath);
        CtClass cc;
        CtMethod cm;
        Map<String,Object> paramMap = null;
        try {
            cc = pool.get(clazz.getName());
            Class<?>[] parameterTypes = method.getParameterTypes();
            int parameterTypesLength = parameterTypes.length;
            if (parameterTypesLength > 0) {
                paramMap = new HashMap<>();
                String[] paramTypeNames = new String[parameterTypesLength];
                for (int i = 0; i < parameterTypesLength; i++)
                    paramTypeNames[i] = parameterTypes[i].getName();
                cm = cc.getDeclaredMethod(method.getName(), pool.get(paramTypeNames));

                MethodInfo methodInfo = cm.getMethodInfo();
                CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

                int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
                for (int i = 0; i < parameterTypesLength; i++) {
                    paramMap.put(attr.variableName(i + pos),parameterTypes[i]);
                }
            }

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        return paramMap;
    }

}
