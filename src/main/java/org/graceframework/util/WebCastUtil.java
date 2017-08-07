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


}
