package org.graceframework.beans.scanner;

import org.graceframework.beans.ClassScanner;
import org.graceframework.beans.Filter;
import org.graceframework.util.ArrayUtil;
import org.graceframework.util.ClassUtil;
import org.graceframework.util.StringUtil;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Tony Liu on 2017/7/31.
 * 类扫描器
 * 考虑使用弱引用构建一个缓存机制，从缓存中获取之前扫描获得Class。
 */
public final class DefaultClassScanner implements ClassScanner {

    private static final Logger logger = LoggerFactory.getLogger(DefaultClassScanner.class);

    @Override
    public Set<Class<?>> scanAllPackage(String packageName) {

        return scanPackage(packageName, null);
    }

    @Override
    public Set<Class<?>> scanPackageByAnnotation(String packageName, final Class<? extends Annotation> annotationClass) {

        return scanPackage(packageName, new Filter<Class<?>>() {
            @Override
            public boolean accept(Class<?> clazz) {
                return clazz.isAnnotationPresent(annotationClass);
            }
        });
    }

    @Override
    public Set<Class<?>> scanPackageBySuper(String packageName, final Class<?> superClass) {

        return scanPackage(packageName, new Filter<Class<?>>() {
            @Override
            public boolean accept(Class<?> clazz) {
                return superClass.isAssignableFrom(clazz) && !clazz.equals(superClass);
            }
        });
    }

    @Override
    public Set<Class<?>> scanPackageGetYouWant(String packageName, Filter<Class<?>> classFilter) {

        return scanPackage(packageName,classFilter);
    }

    /**
     * 真正执行扫描包的入口
     * @param packageName 用户指定的包名
     * @param classFilter 过滤出符合条件的class
     * @return class集合
     */
    private Set<Class<?>> scanPackage(String packageName, Filter<Class<?>> classFilter) {

        if (StringUtil.isBlank(packageName)) {
            packageName = StringUtil.EMPTY;
        }
        Assert.assertTrue("请输入规范的包名...",checkPackageName(packageName));
        if (logger.isDebugEnabled()) {
            logger.debug("正在扫描包 [{}]...", packageName);
        }

        final Set<Class<?>> classSet = new HashSet<>();
        final Enumeration<URL> urls = getClassURL(packageName);
        while (urls.hasMoreElements()) {

            URL url = urls.nextElement();
            String protocol = url.getProtocol();
            if (StringUtil.FILE.equals(protocol)) {

                //解决路径中空格和中文导致的Jar找不到
                String classPath = resolveSpaceAndChinese(url.getPath());
                //解析文件
                parseFileClass(classSet, classPath, packageName, classFilter);
            } else if (StringUtil.JAR.equals(protocol)) {

                //解析jar包
                parseJarClass(classSet, url, classFilter);
            }
        }

        return classSet;
    }

    /**
     * 解析jar包
     * @param classSet 需要返回的类集合
     * @param url jar的url路径
     * @param classFilter 过滤器
     */
    private void parseJarClass(Set<Class<?>> classSet, URL url, Filter<Class<?>> classFilter) {

        try {

            JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
            JarFile jarFile = null;
            try {
                jarFile = jarURLConnection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {

                    JarEntry jarEntry = entries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (jarEntryName.endsWith(StringUtil.DOT_CLASS)) {

                        String className = getClassName(jarEntryName).replaceAll(StringUtil.SLASH, StringUtil.DOT);
                        fillClass(classSet,className,classFilter);
                    }
                }
            } finally {
                if (jarFile != null) {
                    jarFile.close();
                }
            }

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * 解析file
     * @param classSet 需要返回的类集合
     * @param classPath 文件路径
     * @param packageName 包路径（当前文件夹路径）
     * @param classFilter 过滤器
     */
    private void parseFileClass(Set<Class<?>> classSet, String classPath, String packageName, Filter<Class<?>> classFilter) {

        File[] files = new File(classPath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {

                return (file.isFile() && file.getName().endsWith(StringUtil.DOT_CLASS)) || file.isDirectory();
            }
        });
        if (ArrayUtil.isNotEmpty(files)) {
            for (File file : files) {
                String fileName = file.getName();
                if (file.isFile()) {

                    String className = getClassName(fileName);
                    fillClass(classSet,packageName + StringUtil.DOT +className,classFilter);
                } else {

                    String subClassPath = fileName;
                    subClassPath = classPath + StringUtil.SLASH + subClassPath;
                    String subPackageName = fileName;
                    if (StringUtil.isNotBlank(packageName)) {
                        subPackageName = packageName + StringUtil.DOT + subPackageName;
                    }

                    //递归
                    parseFileClass(classSet,subClassPath,subPackageName,classFilter);
                }
            }
        }

    }

    /**
     * 把加载好的类封装到集合中
     * @param classSet 需要返回的类集合
     * @param className 类全名
     * @param classFilter 过滤器
     */
    private void fillClass(Set<Class<?>> classSet, String className, Filter<Class<?>> classFilter) {

        Class<?> clazz = ClassUtil.loadClass(className, false);
        if (classFilter == null || classFilter.accept(clazz)) {

            classSet.add(clazz);
        }
    }

    /**
     * 去除文件后缀
     * @param fileName 带后缀的文件名
     * @return 类全名
     */
    private String getClassName(String fileName) {

        return fileName.substring(0, fileName.lastIndexOf(StringUtil.DOT));
    }

    /**
     * 解决路径中的空格和中文
     */
    private String resolveSpaceAndChinese(String classPath) {

        String path;
        try {
            path = URLDecoder.decode(classPath, Charset.defaultCharset().name());
        } catch (UnsupportedEncodingException e) {

            throw new RuntimeException(e);
        }

        return path;
    }

    /**
     * 获取class的资源路径
     */
    private Enumeration<URL> getClassURL(String packageName) {

        String packagePath = packageName.replace(StringUtil.DOT, StringUtil.SLASH);
        Enumeration<URL> resources;
        try {
            resources = ClassUtil.getClassLoader().getResources(packagePath);
        } catch (IOException e) {

            throw new RuntimeException("获取类路径失败...",e);
        }
        return resources;
    }

    /**
     * 验证包名
     * 只允许xxx.xxx.xxx形式的命名
     */
    private boolean checkPackageName(String packageName) {
        return packageName.matches("^[A-Za-z]+(\\.[A-Za-z]+)*$");
    }

}
