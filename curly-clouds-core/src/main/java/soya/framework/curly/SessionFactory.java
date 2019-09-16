package soya.framework.curly;

public interface SessionFactory {
    Session create(String uri, Object[] args, Dispatcher caller);
}
