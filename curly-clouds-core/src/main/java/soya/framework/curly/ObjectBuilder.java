package soya.framework.curly;

public interface ObjectBuilder<T> {
    Class<T> getResultType();

    T create();
}
