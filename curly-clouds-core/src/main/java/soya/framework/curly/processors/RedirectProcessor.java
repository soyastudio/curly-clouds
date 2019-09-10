package soya.framework.curly.processors;

import soya.framework.curly.DispatchRegistration;
import soya.framework.curly.Operation;
import soya.framework.curly.Processor;
import soya.framework.curly.Session;
import soya.framework.curly.support.DispatchServiceSingleton;

public class RedirectProcessor implements Processor {

    private final String uri;

    public RedirectProcessor(String uri) {
        this.uri = uri;
    }

    @Override
    public void process(Session session) throws Exception {
        DispatchRegistration registration = (DispatchRegistration) DispatchServiceSingleton.getInstance();
        Operation mp = registration.getProcessor(uri);
        mp.process(session);
    }
}
