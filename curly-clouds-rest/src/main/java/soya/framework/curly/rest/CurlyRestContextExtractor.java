package soya.framework.curly.rest;

public interface CurlyRestContextExtractor<T> {
    T extract(CurlyRestContext context);
}
