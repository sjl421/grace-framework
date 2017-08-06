package org.graceframework.util;

/**
 * Created by Tony Liu on 2017/7/31.
 */
public class StringUtil {

    private StringUtil() {
    }

    public static final String DOT = ".";
    public static final String SLASH = "/";
    public static final String EMPTY = "";
    /**
     * url的空格编码
     */
    public static final String URL_SPACE = "%20";
    public static final String SPACE = " ";
    public static final String FILE = "file";
    public static final String JAR = "jar";
    public static final String DOT_CLASS = ".class";
    /**
     * 字符串是否为空白 空白的定义如下： <br>
     * 1、为null <br>
     * 2、为不可见字符（如空格）<br>
     * 3、""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isBlank(CharSequence str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (!isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 字符串是否为非空白 空白的定义如下： <br>
     * 1、不为null <br>
     * 2、不为不可见字符（如空格）<br>
     * 3、不为""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为非空
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }


    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     * @param c 字符
     * @return 是否空白符
     */
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c);
    }


    public static String toLowerClassNameFirstLetter(Class<?> clazz) {

        StringBuilder sb = new StringBuilder();
        String name = clazz.getSimpleName();
        String firstLetter = name.substring(0,1);

        return sb.append(firstLetter.toLowerCase()).append(name.substring(1)).toString();

    }

    /**
     * 字符串匹配
     * @param request 客户端请求地址
     * @param server 服务端映射地址
     * @return 匹配成功 或失败
     */
    public static boolean matcher(String request, String server) {

        if (!request.startsWith("/")) {
            request = "/" + request;
        }
        if (!server.startsWith("/")) {
            server = "/" + server;
        }
        if (request.equals(server)) {

            return true;
        } else {

            String[] split = request.split("/");
            int v1 = split.length;
            String[] split2 = server.split("/");
            int v2 = split2.length;
            if (v1 == v2) {
                for (int i = 0; i < v2; i++) {
                    String s1 = split[i];
                    String s2 = split2[i];
                    if ("*".equals(s2) || "**".equals(s2)) {
                        continue;
                    }
                    if (!s1.equals(s2)) {
                        return false;
                    }
                }
                return true;
            }
            if (v1 > v2) {
                if (!server.endsWith("/**")) {
                    return false;
                }
                for (int i = 0; i < v2; i++) {
                    String s1 = split[i];
                    String s2 = split2[i];
                    if ("*".equals(s2) || "**".equals(s2)) {
                        continue;
                    }
                    if (!s1.equals(s2)) {
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 整理映射地址，把地址整理成 /xx/xx 这种形式
     * @param path 地址
     * @return 整理后的地址
     */
    public static String doPath(String path) {

        if (StringUtil.isNotBlank(path)) {
            StringBuilder sb = new StringBuilder(path);
            if (!path.startsWith(StringUtil.SLASH)) {
                sb.insert(0,StringUtil.SLASH);
            }
            if (path.endsWith(StringUtil.SLASH)) {

                return sb.substring(0,sb.length() - 1);
            }
            return sb.toString();
        }
        return StringUtil.EMPTY;
    }
}
