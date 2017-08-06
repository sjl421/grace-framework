package org.graceframework.beans.error;

/**
 * 初始化错误
 *
 * @author huangyong
 * @since 2.2
 */
public class InitialContextError extends Error {

    public InitialContextError(String message) {
        super(message);
    }

    public InitialContextError(String message, Throwable cause) {
        super(message, cause);
    }


}
