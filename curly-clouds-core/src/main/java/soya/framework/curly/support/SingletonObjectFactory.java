package soya.framework.curly.support;

import soya.framework.curly.ObjectFactory;

public abstract class SingletonObjectFactory implements ObjectFactory {
    protected static ObjectFactory INSTANCE;

    public static ObjectFactory getInstance() {
        return INSTANCE;
    }

    protected SingletonObjectFactory() {
        if(INSTANCE != null) {
            throw new IllegalStateException("ObjectFactory already exist.");
        }

        INSTANCE = this;

    }

}
