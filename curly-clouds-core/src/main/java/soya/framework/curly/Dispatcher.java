package soya.framework.curly;

public abstract class Dispatcher {
    protected abstract Object dispatch(String methodId, Object[] args) throws DispatchException;

}
