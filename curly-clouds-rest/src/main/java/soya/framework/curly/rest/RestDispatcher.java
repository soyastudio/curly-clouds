package soya.framework.curly.rest;

import soya.framework.curly.Dispatcher;
import soya.framework.curly.support.DispatchServiceSingleton;

public abstract class RestDispatcher extends Dispatcher {

    @Override
    protected Object dispatch(String methodId, Object[] args) {
        return DispatchServiceSingleton.getInstance().dispatch(this, methodId, args);
    }
}
