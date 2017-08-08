package org.graceframework.util;

/**
 * Created by Tong on 2017/8/7.
 * 把request的参数转换成控制器方法上对应的参数类型
 */
public class WebCastUtil {


    public static boolean castBoolean(String param) {

        return Boolean.parseBoolean(param);
    }

    public static Integer castInteger(String param) {

        return Integer.parseInt(param);
    }

    public static Long castLong(String param) {

        return Long.parseLong(param);
    }

    public static Double castDouble(String param) {

        return Double.parseDouble(param);
    }

    public static Float castFloat(String param) {

        return Float.parseFloat(param);
    }

    /**
     * 判断参数是否为基本类型
     */
    public static Object getValue(Class<?> type, String value){

        if (ClassUtil.isString(type)) {
            return value;
        } else if (ClassUtil.isInt(type)) {
            return castInteger(value);
        } else if (ClassUtil.isDouble(type)) {
            return castDouble(value);
        } else if (ClassUtil.isLong(type)) {
            return castLong(value);
        } else if (ClassUtil.isFloat(type)) {
            return castFloat(value);
        } else if (ClassUtil.isBoolean(type)) {
            return castBoolean(value);
        }

        return null;
    }

}
