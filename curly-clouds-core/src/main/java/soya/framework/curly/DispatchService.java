package soya.framework.curly;

public interface DispatchService {
    Object dispatch(Object caller, String uri, Object[] args) throws DispatchException;
}
