package soya.framework.curly.support;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import soya.framework.commons.reflect.CloneUtils;
import soya.framework.curly.DataObject;
import soya.framework.curly.Evaluation;
import soya.framework.curly.Invocation;
import soya.framework.curly.Session;
import soya.framework.curly.util.GsonUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SessionSupport implements Session {

    public static final String INPUT_DATA_KEY = "IN";
    public static final String OUTPUT_DATA_KEY = "OUT";
    public static final String ATTRIBUTE_DATA_KEY = "ATTR";

    private final String uri;
    private final String id;
    private final long createdTime;
    private Invocation invocation;

    private DataObject currentState;
    private Map<String, ObjectWrapper> attributes = new ConcurrentHashMap<>();

    private long lastUpdatedTime;

    private transient Evaluation evaluation;

    protected SessionSupport(String uri) {
        this.uri = uri;
        this.id = UUID.randomUUID().toString();
        this.createdTime = System.currentTimeMillis();
        this.lastUpdatedTime = System.currentTimeMillis();
    }

    protected SessionSupport(Invocation invocation) {
        this.uri = invocation.getUri();
        this.invocation = invocation;
        this.id = UUID.randomUUID().toString();
        this.createdTime = System.currentTimeMillis();
        this.lastUpdatedTime = System.currentTimeMillis();
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public Invocation getInvocation() {
        return invocation;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public Object get(String attrName) {
        if (!attributes.containsKey(attrName)) {
            return null;
        } else {
            return attributes.get(attrName).getWrapperedObject();
        }
    }

    @Override
    public synchronized void set(String attrName, Object attrValue) throws IllegalStateException {
        if (attributes.containsKey(attrName) && attributes.get(attrName).immutable) {
            throw new IllegalStateException("Attribute '" + attrName + "' is immutable.");

        } else if (attrValue == null) {
            attributes.remove(attrName);

        } else {
            attributes.put(attrName, new ObjectWrapper(attrValue));

        }
    }

    @Override
    public synchronized void setImmutable(String attrName, Object attrValue) throws IllegalStateException {
        if (attributes.containsKey(attrName) && attributes.get(attrName).immutable) {
            throw new IllegalStateException("Attribute '" + attrName + "' is immutable.");

        } else if (attrValue == null) {
            throw new IllegalArgumentException("Immutable attribute cannot be null.");

        } else if (attrValue instanceof Cloneable) {
            throw new IllegalArgumentException("Immutable attribute should be cloneable.");

        } else {
            attributes.put(attrName, new ObjectWrapper(attrValue, true));
        }
    }

    @Override
    public DataObject getCurrentState() {
        return currentState;
    }

    @Override
    public synchronized void updateState(DataObject state) throws IllegalStateException {
        if (evaluation != null) {
            throw new IllegalStateException("Illegal state: session is in evaluation state.");
        }

        this.currentState = state;
        this.lastUpdatedTime = System.currentTimeMillis();
    }

    @Override
    public long getLastUpdatedTime() {
        return this.lastUpdatedTime;
    }

    @Override
    public void startEvaluation() {
        if (evaluation != null) {
            throw new IllegalStateException("Evaluation is already started.");
        }

        this.evaluation = new DefaultEvaluation(this);
    }

    @Override
    public void endEvaluation() {
        this.evaluation = null;
    }

    @Override
    public String getAsJsonString() {
        Gson gson = new Gson();
        JsonObject root = new JsonObject();

        root.addProperty("_uri", getUri());
        root.addProperty("_id", getId());
        root.addProperty("_createdTime", getCreatedTime());
        root.addProperty("_lastUpdatedTime", getLastUpdatedTime());
/*

        if (inputData != null) {
            root.add(INPUT_DATA_KEY, inputData.getAsJsonElement());
        }
*/

        if (currentState != null) {
            root.add(OUTPUT_DATA_KEY, GsonUtils.toJsonElement(currentState));
        }

        if (attributes != null && !attributes.isEmpty()) {
            JsonObject attr = new JsonObject();
            attributes.entrySet().forEach(entry -> attr.add(entry.getKey(), GsonUtils.toJsonElement(entry.getValue())));
            root.add(ATTRIBUTE_DATA_KEY, attr);
        }

        return gson.toJson(root);
    }

    static class ObjectWrapper {
        private Object wrapperedObject;
        private boolean immutable;

        public ObjectWrapper(Object wrapperedObject) {
            this.wrapperedObject = wrapperedObject;
        }

        public ObjectWrapper(Object wrapperedObject, boolean immutable) {
            this.wrapperedObject = wrapperedObject;
            this.immutable = immutable;
        }

        public Object getWrapperedObject() {
            if (!immutable || (wrapperedObject != null && CloneUtils.isImmutable(wrapperedObject.getClass()))) {
                return wrapperedObject;
            } else {
                return CloneUtils.deepClone(wrapperedObject);
            }
        }
    }

    static class DefaultEvaluation implements Evaluation {
        private final Session session;
        private DataObject value;
        private Map<String, Object> attributes;

        private DefaultEvaluation(Session session) {
            this.session = session;

            this.value = CloneUtils.deepClone(session.getCurrentState());
            this.attributes = Collections.synchronizedMap(new HashMap<>());
        }

        @Override
        public Session getSession() {
            return session;
        }

        @Override
        public Object get(String attrName) {
            return attributes.get(attrName);
        }

        @Override
        public void set(String attrName, Object attrValue) {
            attributes.put(attrName, attrValue);
        }

        public DataObject getValue() {
            return value;
        }

        public void setValue(DataObject value) {
            this.value = value;
        }
    }
}
