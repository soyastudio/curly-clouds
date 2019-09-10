package soya.framework.curly.processors;

import soya.framework.curly.Processor;
import soya.framework.curly.Session;

public class DebugProcessor implements Processor {
    private String name;

    public DebugProcessor() {
    }

    public DebugProcessor(String name) {
        this.name = name;
    }

    @Override
    public void process(Session session) {
        System.out.println("=============== debug me!");
    }
}
