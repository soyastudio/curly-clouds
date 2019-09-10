package soya.framework.curly.processors.evaluators;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import soya.framework.curly.DataObject;
import soya.framework.curly.Evaluation;
import soya.framework.curly.SessionEvaluator;
import soya.framework.curly.support.JsonData;
import soya.framework.curly.util.GsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class JsonPathAggregator implements SessionEvaluator {

    private final String example;
    private Map<String, String> paths;

    private JsonPathAggregator(String example, Map<String, String> paths) {
        this.example = example;
        this.paths = paths;
    }

    @Override
    public DataObject evaluate(Evaluation evaluation) {
        JsonElement jsonElement = new JsonParser().parse(example);

        String json = evaluation.getSession().getAsJsonString();
        paths.entrySet().forEach(entry -> {
            String path = entry.getValue();
            JsonElement value = GsonUtils.fromJsonPath(entry.getValue(), json);
            setPath(path, value, jsonElement);
        });

        return JsonData.fromJsonElement(jsonElement);
    }

    private void setPath(String path, JsonElement value, JsonElement root) {
        StringTokenizer tokenizer = new StringTokenizer(path, ".");
        JsonElement jsonElement = root;
        JsonObject parent = null;
        String property = null;
        while (tokenizer.hasMoreTokens()) {
            if (!jsonElement.isJsonObject()) {
                throw new IllegalArgumentException("Path '" + path + "' does not match the json example.");

            } else {
                property = tokenizer.nextToken();
                parent = jsonElement.getAsJsonObject();
                jsonElement = parent.get(property);
            }

        }

        if(parent != null) {
            parent.add(property, value);
        }
    }

    public static Builder builder(Object example) {
        return new Builder(new Gson().toJson(example));
    }

    public static Builder builder(String example) {
        return new Builder(example);
    }

    public static class Builder {
        private final String example;
        private Map<String, String> paths = new HashMap<>();

        public Builder(String example) {
            this.example = example;
        }

        public Builder addExpression(String path, String expression) {
            paths.put(path, expression);
            return this;
        }

        public JsonPathAggregator create() {
            return new JsonPathAggregator(example, paths);
        }
    }
}
