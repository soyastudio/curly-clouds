package soya.framework.curly.rest;

import soya.framework.curly.DispatchContext;
import soya.framework.curly.Operation;
import soya.framework.curly.SessionDeserializer;
import soya.framework.curly.support.DispatchServiceSupport;

@DispatchContext(name = "REST Dispatch Service", schema = {
        "GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"
})
public final class RestDispatchService extends DispatchServiceSupport {

    public RestDispatchService() {

    }

    @Override
    public Operation create(String uri) {
        return null;
    }
}
