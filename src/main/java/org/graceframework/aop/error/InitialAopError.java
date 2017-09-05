package org.graceframework.aop.error;

/**
 * Created by Tong on 2017/8/2.
 */
public class InitialAopError extends Error {

    public InitialAopError(String message) {
        super(message);
    }

    public InitialAopError(String message, Throwable cause) {
        super(message, cause);
    }
}
