package org.graceframework.beans.scanner;

import org.graceframework.beans.ClassScanner;
import org.graceframework.beans.Filter;
import org.graceframework.util.StringUtil;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by Tong on 2017/8/2.
 * 类扫描器入口 类扫描器在这里实例化 统一调用
 */
public class ClassScannerEntrance {

    private static ClassScannerEntrance instance = new ClassScannerEntrance();
    private static final String packageName = "framework.test";
    private static final ClassScanner scanner = new DefaultClassScanner();
    private ClassScannerEntrance() {}

    public static ClassScannerEntrance getInstance() {

        return instance;
    }

    public Set<Class<?>> scanAllPackage() {

        return scanner.scanAllPackage(packageName);
    }

    public Set<Class<?>> scanPackage(String pkName) {

        return scanner.scanAllPackage(pkName);
    }

    public Set<Class<?>> scanPackageByAnnotation(String pkName, final Class<? extends Annotation> annotationClass) {

        return scanner.scanPackageByAnnotation(StringUtil.isNotBlank(pkName) ? pkName : packageName, annotationClass);
    }

    public Set<Class<?>> scanPackageBySuper(String pkName, final Class<?> superClass) {

        return scanner.scanPackageBySuper(StringUtil.isNotBlank(pkName) ? pkName : packageName, superClass);
    }

    public Set<Class<?>> scanPackageGetYouWant(String pkName, Filter<Class<?>> classFilter) {

        return scanner.scanPackageGetYouWant(StringUtil.isNotBlank(pkName) ? pkName : packageName,classFilter);
    }
}
