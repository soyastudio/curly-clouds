package soya.framework.curly.support;

import soya.framework.curly.DispatchContext;
import soya.framework.curly.DispatchException;

@DispatchContext(schema = "direct://")
public class DirectDispatchService extends UriDispatchService {
    @Override
    public void registerSubjects(Class<?>[] subjects) {

    }

    @Override
    public Object dispatch(Object caller, String uri, Object[] args) throws DispatchException {
        return null;
    }
}
