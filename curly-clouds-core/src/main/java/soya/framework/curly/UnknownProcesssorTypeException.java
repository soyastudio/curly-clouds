package soya.framework.curly;

public class UnknownProcesssorTypeException extends ProcessException {
    public UnknownProcesssorTypeException() {
    }

    public UnknownProcesssorTypeException(String message) {
        super(message);
    }

    public UnknownProcesssorTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownProcesssorTypeException(Throwable cause) {
        super(cause);
    }
}
