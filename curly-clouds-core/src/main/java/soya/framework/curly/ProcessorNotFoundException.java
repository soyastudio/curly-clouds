package soya.framework.curly;

public class ProcessorNotFoundException extends DispatchException {

    public ProcessorNotFoundException() {
    }

    public ProcessorNotFoundException(String message) {
        super(message);
    }

    public ProcessorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessorNotFoundException(Throwable cause) {
        super(cause);
    }
}
