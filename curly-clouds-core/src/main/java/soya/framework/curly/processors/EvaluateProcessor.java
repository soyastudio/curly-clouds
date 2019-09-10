package soya.framework.curly.processors;

import soya.framework.curly.Processor;
import soya.framework.curly.Session;

public class EvaluateProcessor implements Processor {


    @Override
    public void process(Session session) throws Exception {
        session.startEvaluation();

        session.endEvaluation();
    }


}
