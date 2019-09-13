package soya.framework.curly.support;

import soya.framework.curly.DispatchContext;
import soya.framework.curly.DispatchException;
import soya.framework.curly.Operation;

@DispatchContext(name = "Direct Dispatch Service", schema = "direct://")
public class DirectDispatchService extends DispatchServiceSupport {

    @Override
    public void registerSubjects(Class<?>[] subjects) {

    }

    @Override
    public Operation getProcessor(String uri) {
        System.out.println("------------------- : " + uri);
        String className = uri.substring("direct://".length());
        try {
            return (Operation) Class.forName(className).newInstance();

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
