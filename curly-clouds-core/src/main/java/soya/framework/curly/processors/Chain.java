package soya.framework.curly.processors;

import com.google.common.collect.ImmutableList;
import soya.framework.curly.Processor;
import soya.framework.curly.Session;

import java.util.ArrayList;
import java.util.List;

public final class Chain implements Processor {

    private final ImmutableList<Processor> processors;

    private Chain(List<Processor> processors) {
        this.processors = ImmutableList.copyOf(processors);
    }

    @Override
    public void process(Session session) throws Exception {
        for (Processor p : processors) {
            p.process(session);
        }
    }

    public static ChainBuilder builder() {
        return new ChainBuilder();
    }

    public static class ChainBuilder {
        protected List<Processor> processors = new ArrayList<>();

        private ChainBuilder() {
        }

        public ChainBuilder add(Processor processor) {
            processors.add(processor);
            return this;
        }

        public Chain build() {
            return new Chain(processors);
        }
    }

}
