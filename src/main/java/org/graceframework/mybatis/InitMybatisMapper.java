package org.graceframework.mybatis;

import org.graceframework.InstanceFactory;
import org.graceframework.beans.BeanFactory;
import org.graceframework.beans.scanner.ClassScannerEntrance;
import org.graceframework.proxy.ProxyFactory;

import java.util.Set;

/**
 * Created by Tong on 2017/9/5.
 * 把mybatis的mapper实例封装进beanFactory
 */
public class InitMybatisMapper {

    private static final BeanFactory beanFactory = InstanceFactory.getBeanFactory();
    private static final ClassScannerEntrance scanner = ClassScannerEntrance.getInstance();

    static {

        Set<Class<?>> mapperInterfaces = scanner.scanPackage("com.test.demo.mybatis.dao");
        for (Class<?> mapperInterface : mapperInterfaces) {
            beanFactory.setBean(mapperInterface, ProxyFactory.createMapperInstanceProxy(mapperInterface));
        }
    }

}
