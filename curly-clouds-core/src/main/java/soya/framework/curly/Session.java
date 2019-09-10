package soya.framework.curly;

public interface Session extends JsonCompatible {
    // Metadata:
    String getUri();

    String getId();

    long getCreatedTime();

    // Initial state:
    Invocation getInvocation();

    // Attribute:
    Object get(String attrName);

    void set(String attrName, Object attrValue) throws IllegalStateException;

    void setImmutable(String attrName, Object attrValue) throws IllegalStateException;

    // Current State
    DataObject getCurrentState();

    void updateState(DataObject state) throws IllegalStateException;

    long getLastUpdatedTime();

    // Evaluation:
    void startEvaluation();

    void endEvaluation();
}
