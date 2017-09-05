package org.graceframework.mvc.core;


/**
 * Created by Tony Liu on 2017/8/3.
 */
public class Requester {
    /**
     * 请求方式 get 或者post
     */
    private String method;
    private String path;

    public Requester(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getMethod() {
        return method == null ? "null" : path;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path == null ? "null" : path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj instanceof Requester) {

                Requester requester = (Requester)obj;
                if (requester.path.endsWith(this.getPath()) && this.getMethod().equals(requester.getMethod())) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = result * 31 + this.getPath().hashCode();
        result = result * 31 + this.getMethod().hashCode();

        return result;
    }
}
