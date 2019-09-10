package soya.framework.curly.processors;

import soya.framework.curly.Processor;
import soya.framework.curly.Session;

public final class ExceptionTestProcessor implements Processor {
    private final Exception exception;

    public ExceptionTestProcessor(Exception exception) {
        this.exception = exception;
    }

    @Override
    public void process(Session session) throws Exception {
        if(exception != null) {
            throw exception;
        }
    }
}
