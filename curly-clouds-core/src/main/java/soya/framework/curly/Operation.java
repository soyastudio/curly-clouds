package soya.framework.curly;

public interface Operation extends Processor {

    @Override
    void process(Session session) throws ProcessException;
}
