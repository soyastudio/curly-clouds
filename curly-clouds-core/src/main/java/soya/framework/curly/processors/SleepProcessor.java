package soya.framework.curly.processors;

import soya.framework.curly.Processor;
import soya.framework.curly.Session;

public final class SleepProcessor implements Processor {
    private final long sleep;

    public SleepProcessor(long sleep) {
        this.sleep = sleep;
        if(sleep <= 0) {
            throw new IllegalArgumentException("Sleep time must be positive!");
        }
    }

    @Override
    public void process(Session session) throws InterruptedException {
        Thread.sleep(sleep);
    }
}
