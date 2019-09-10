package soya.framework.curly.rest;

import soya.framework.curly.DispatchException;

public class RestMethodException extends DispatchException {
    public RestMethodException() {
    }

    public RestMethodException(String message) {
        super(message);
    }

    public RestMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestMethodException(Throwable cause) {
        super(cause);
    }
}
