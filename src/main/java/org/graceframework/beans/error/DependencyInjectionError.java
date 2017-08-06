package org.graceframework.beans.error;

/**
 * Created by Tong on 2017/8/2.
 */
public class DependencyInjectionError extends Error {

    public DependencyInjectionError(String message) {
        super(message);
    }

    public DependencyInjectionError(String message, Throwable cause) {
        super(message, cause);
    }
}
