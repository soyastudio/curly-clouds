package soya.framework.curly;

import java.lang.reflect.Type;

public interface ObjectFactory {
    Object fromData(String json, Type type) throws ObjectBuilderException;
}
