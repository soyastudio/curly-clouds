package soya.framework.curly.support;

import soya.framework.curly.Operation;
import soya.framework.curly.ProcessException;
import soya.framework.curly.Processor;
import soya.framework.curly.Session;

import java.util.logging.Logger;

public abstract class OperationSupport<T extends OperationSupport> implements Operation {
    @Override
    public void process(Session session) throws ProcessException {
        preProcess(session);

        try {
            execute(session);
            postProcess(session);

        } catch (Exception e) {
            onError(e, session);
        }
    }

    protected void preProcess(Session session) throws ProcessException {

    }

    protected void postProcess(Session session) throws ProcessException {

    }

    protected void onError(Exception e, Session session) throws ProcessException {
        Logger.getLogger("EXCEPTION FROM: '" + session.getUri() + "'").severe(e.getClass().getName() + "[" + e.getMessage() + "]");

        if(e instanceof ProcessException) {
            throw (ProcessException)e;
        } else {
            throw new ProcessException(e);
        }
    }

    protected abstract void execute(Session session) throws Exception;



    public T process(Session session, Processor processor) throws Exception{
        processor.process(session);
        return (T)this;
    }

    public T startEvaluation(Session session, Processor processor) {
        session.startEvaluation();
        return (T)this;
    }

    public T endEvaluation(Session session, Processor processor) {
        session.endEvaluation();
        return (T)this;
    }
}
