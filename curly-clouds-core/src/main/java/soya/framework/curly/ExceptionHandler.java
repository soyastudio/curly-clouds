package soya.framework.curly;

public interface ExceptionHandler<E extends Exception> {
    void onException(E e);
}
