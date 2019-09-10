package soya.framework.curly.processors;

import soya.framework.curly.Operation;
import soya.framework.curly.Processor;
import soya.framework.curly.Session;

import java.util.logging.Logger;

public class EchoProcessor implements Processor {
    private static Logger logger = Logger.getLogger(EchoProcessor.class.getName());

    private Class<? extends Operation> caller;

    public EchoProcessor() {
    }

    public EchoProcessor(Class<? extends Operation> caller) {
        this.caller = caller;
    }

    @Override
    public void process(Session session) {
        StringBuilder builder = new StringBuilder(session.getUri());
        if (caller != null) {
            builder.append(" -> ").append(caller.getName());
        }
        logger.info(builder.toString());
    }
}
