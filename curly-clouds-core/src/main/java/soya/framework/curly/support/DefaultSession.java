package soya.framework.curly.support;

import soya.framework.curly.Invocation;
import soya.framework.curly.Processor;

public final class DefaultSession extends SessionSupport {

    private transient Evaluation evaluation;

    protected DefaultSession(String uri) {
        super(uri);
    }

    protected DefaultSession(Invocation invocation) {
        super(invocation);
    }


    // ---------------------
    public void setDefaultEvaluationProcessor(boolean updateStateBeforeProcess, Processor processor) {
        if (evaluation == null) {
            throw new IllegalStateException("Not in evaluation state.");
        }

        evaluation.setDefaultProcessor(updateStateBeforeProcess, processor);
    }

    public void addCondition(String expression, boolean updateStateBeforeProcess, Processor processor) {
        if (evaluation == null) {
            throw new IllegalStateException("Not in evaluation state.");
        }

        evaluation.addCondition(expression, updateStateBeforeProcess, processor);
    }

    public void processEvaluation() {
        if (evaluation == null) {
            throw new IllegalStateException("Not in evaluation state.");
        }

        evaluation.process(this);
        this.evaluation = null;
    }


}
