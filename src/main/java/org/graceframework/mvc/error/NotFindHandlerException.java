package org.graceframework.mvc.error;

/**
 * Created by Tong on 2017/8/5.
 */
public class NotFindHandlerException extends RuntimeException {

    public NotFindHandlerException(String msg) {
        super(msg);
    }
}
