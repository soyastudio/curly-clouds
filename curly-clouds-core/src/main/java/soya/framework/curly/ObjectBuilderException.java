package soya.framework.curly;

public class ObjectBuilderException extends Exception {
    public ObjectBuilderException() {
    }

    public ObjectBuilderException(String message) {
        super(message);
    }

    public ObjectBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectBuilderException(Throwable cause) {
        super(cause);
    }
}
