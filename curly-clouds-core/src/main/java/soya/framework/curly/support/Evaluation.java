package soya.framework.curly.support;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import soya.framework.curly.DataObject;
import soya.framework.curly.Processor;
import soya.framework.curly.Session;
import soya.framework.curly.util.GsonUtils;

import java.util.ArrayList;
import java.util.List;

public class Evaluation implements Processor {

    private DataObject value;
    private Condition defaultProcessor;
    private List<Condition> conditions = new ArrayList<>();

    public Evaluation(DataObject value) {
        this.value = value;
    }

    public DataObject getValue() {
        return value;
    }

    public Condition getDefaultProcessor() {
        return defaultProcessor;
    }

    public void setDefaultProcessor(Processor processor) {
        this.defaultProcessor = new Condition(null, false, processor);
    }

    public void setDefaultProcessor(boolean updateStateBeforeProcess, Processor processor) {
        this.defaultProcessor = new Condition(null, updateStateBeforeProcess, processor);
    }

    public void addCondition(String expression, Processor processor) {
        conditions.add(new Condition(expression, false, processor));
    }

    public void addCondition(String expression, boolean updateStateBeforeProcess, Processor processor) {
        conditions.add(new Condition(expression, updateStateBeforeProcess, processor));
    }

    public void process(Session session) {
        String jsonString = GsonUtils.toJson(value);

        DocumentContext context = JsonPath.parse(jsonString);
        for (Condition c : conditions) {
            if (c.match(context)) {
                if (c.updateStateBeforeProcess) {
                    session.updateState(value);
                }

                try {
                    c.processor.process(session);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        if (defaultProcessor != null) {
            if(defaultProcessor.updateStateBeforeProcess) {
                session.updateState(value);
            }

            try {
                defaultProcessor.processor.process(session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Condition {

        private final String expression;
        private final boolean updateStateBeforeProcess;
        private final Processor processor;

        public Condition(String expression, boolean updateStateBeforeProcess, Processor processor) {
            this.expression = expression;
            this.updateStateBeforeProcess = updateStateBeforeProcess;
            this.processor = processor;
        }

        public boolean match(DocumentContext context) {
            List<Object> dataList = context.read(expression);
            return dataList != null && !dataList.isEmpty();
        }
    }
}
