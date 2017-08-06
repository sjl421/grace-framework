package org.graceframework.beans.scanner;

import org.graceframework.beans.ClassScanner;
import org.graceframework.beans.Filter;

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

    public Set<Class<?>> scanPackageByAnnotation(final Class<? extends Annotation> annotationClass) {

        return scanner.scanPackageByAnnotation(packageName, annotationClass);
    }

    public Set<Class<?>> scanPackageBySuper(final Class<?> superClass) {

        return scanner.scanPackageBySuper(packageName, superClass);
    }

    public Set<Class<?>> scanPackageGetYouWant(Filter<Class<?>> classFilter) {

        return scanner.scanPackageGetYouWant(packageName,classFilter);
    }
}
