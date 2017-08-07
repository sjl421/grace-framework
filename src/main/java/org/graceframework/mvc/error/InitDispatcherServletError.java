package org.graceframework.mvc.error;

/**
 * Created by Tony Liu on 2017/8/7.
 */
public class InitDispatcherServletError extends Error {

    public InitDispatcherServletError(String message) {
        super(message);
    }

    public InitDispatcherServletError(String message, Throwable cause) {
        super(message, cause);
    }
}
