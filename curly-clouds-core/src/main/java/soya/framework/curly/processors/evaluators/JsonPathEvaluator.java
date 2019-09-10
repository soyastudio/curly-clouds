package soya.framework.curly.processors.evaluators;

import com.google.gson.JsonElement;
import soya.framework.curly.DataObject;
import soya.framework.curly.Evaluation;
import soya.framework.curly.Session;
import soya.framework.curly.SessionEvaluator;
import soya.framework.curly.support.JsonData;
import soya.framework.curly.util.GsonUtils;

public final class JsonPathEvaluator implements SessionEvaluator {

    private final String expression;

    public JsonPathEvaluator(String expression) {
        this.expression = expression;
    }


    public DataObject evaluate(Session session) {
        JsonElement jsonElement = GsonUtils.fromJsonPath(expression, session.getAsJsonString());
        return JsonData.fromJsonElement(jsonElement);
    }

    @Override
    public DataObject evaluate(Evaluation evaluation) {
        // TODO:
        return null;
    }
}
