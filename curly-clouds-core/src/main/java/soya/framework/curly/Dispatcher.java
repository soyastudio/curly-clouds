package soya.framework.curly;

import soya.framework.curly.support.DefaultSession;
import soya.framework.curly.support.DispatchServiceSingleton;
import soya.framework.curly.support.DispatchServiceSupport;

public abstract class Dispatcher {
    protected Object dispatch(String uri, Object[] args) throws DispatchException {
        return DispatchServiceSingleton.getInstance().dispatch(uri, args, this);
    }

}
