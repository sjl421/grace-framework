package org.graceframework.beans;

/**
 * Created by Tony Liu on 2017/7/31.
 */
public interface Filter<T> {

    boolean accept(T t);
}
