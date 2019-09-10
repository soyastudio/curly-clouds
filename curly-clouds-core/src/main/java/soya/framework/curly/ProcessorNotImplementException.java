package soya.framework.curly;

public class ProcessorNotImplementException extends Exception {
    public ProcessorNotImplementException(Class<? extends Operation> processor) {
        super("Processor '" + processor.getName() + "' is not implemented.");
    }
}
