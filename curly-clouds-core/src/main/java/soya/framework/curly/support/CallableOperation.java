package soya.framework.curly.support;

import soya.framework.curly.DispatchExecutor;
import soya.framework.curly.Session;

import java.util.concurrent.Future;

public abstract class CallableOperation<T extends CallableOperation> extends OperationSupport<T> {

    public Future<Session> call(Session session, DispatchExecutor executor)  {
        return executor.submit(() -> {
            process(session);
            return session;
        });
    }
}
