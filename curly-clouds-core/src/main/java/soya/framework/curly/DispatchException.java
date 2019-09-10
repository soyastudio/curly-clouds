package soya.framework.curly;

public class DispatchException extends RuntimeException {

    public DispatchException() {
    }

    public DispatchException(String message) {
        super(message);
    }

    public DispatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public DispatchException(Throwable cause) {
        super(cause);
    }
}
