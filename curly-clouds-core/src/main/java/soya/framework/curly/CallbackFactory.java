package soya.framework.curly;

public interface CallbackFactory<T extends Invocation> {
    Callback create(T invocation);
}
