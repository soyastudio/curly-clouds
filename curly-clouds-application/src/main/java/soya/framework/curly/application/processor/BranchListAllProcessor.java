package soya.framework.curly.application.processor;

import org.springframework.stereotype.Component;
import soya.framework.curly.ProcessorNotImplementException;
import soya.framework.curly.Session;
import soya.framework.curly.processors.*;
import soya.framework.curly.rest.RestOperation;

@Component("GET://branch/list")
public class BranchListAllProcessor extends RestOperation {

    @Override
    public void execute(Session session) throws Exception {
        process(session, new EchoProcessor(getClass()))
                .process(session, new SleepProcessor(1000l))
                .process(session, new ResourceReaderProcessor("mock/branch/list_all_resp.json"));
    }
}
