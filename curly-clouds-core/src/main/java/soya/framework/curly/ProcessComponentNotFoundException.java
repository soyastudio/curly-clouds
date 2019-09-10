package soya.framework.curly;

public class ProcessComponentNotFoundException extends ProcessException {
    public ProcessComponentNotFoundException() {
    }

    public ProcessComponentNotFoundException(String message) {
        super(message);
    }

    public ProcessComponentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessComponentNotFoundException(Throwable cause) {
        super(cause);
    }
}
