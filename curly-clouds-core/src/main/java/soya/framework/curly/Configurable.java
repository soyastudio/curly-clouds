package soya.framework.curly;

public interface Configurable {
    String[] propertyNames();

    void configure(String key, String value);
}
