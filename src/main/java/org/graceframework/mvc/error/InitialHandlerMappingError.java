package org.graceframework.mvc.error;

/**
 * Created by Tong on 2017/8/2.
 */
public class InitialHandlerMappingError extends Error {

    public InitialHandlerMappingError(String message) {
        super(message);
    }

    public InitialHandlerMappingError(String message, Throwable cause) {
        super(message, cause);
    }
}
