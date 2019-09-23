package soya.framework.curly;

public interface Callback {
    void onSuccess(Session session);

    boolean onFailure(Session session, Throwable throwable);
}
