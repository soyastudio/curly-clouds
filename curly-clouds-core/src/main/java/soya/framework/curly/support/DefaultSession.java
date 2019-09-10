package soya.framework.curly.support;

import soya.framework.curly.Processor;

public class DefaultSession extends SessionSupport {

    private final DispatchMethodInvocation inputData;

    private transient Evaluation evaluation;

    public DefaultSession(String name, Object[] inputs) {
        super(name);

        this.inputData = DispatchMethodInvocation.builder(inputs).create();
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
