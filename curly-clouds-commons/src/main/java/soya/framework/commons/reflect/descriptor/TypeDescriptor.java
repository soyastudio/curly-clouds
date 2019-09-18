package soya.framework.commons.reflect.descriptor;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TypeDescriptor {
    private transient Class<?> javaType;

    private String type;
    private ContainerType containerType;

    private TypeDescriptor(Class<?> javaType) {
        this.javaType = javaType;
        if(javaType.isArray()) {
            this.type = javaType.getComponentType().getTypeName();
            containerType = ContainerType.ARRAY;

        } else if(List.class.isAssignableFrom(javaType)) {

        }
        this.type = javaType.getTypeName();
    }

    public String getType() {
        return type;
    }

    public boolean isInstance(Object obj) {
        return javaType.isInstance(obj);
    }

    public static TypeDescriptor fromClass(Class<?> cls) {
        return new TypeDescriptor(cls);
    }

    public static TypeDescriptor fromType(Type type) {
        TypeDescriptor descriptor = null;
        Class<?> clazz = TypeToken.of(type).getRawType();
        if(Collection.class.isAssignableFrom(clazz)) {
            try {
                Type t = ((ParameterizedType) type).getActualTypeArguments()[0];
                Class<?> c = TypeToken.of(t).getRawType();
                descriptor = new TypeDescriptor(c);
                if (List.class.isAssignableFrom(clazz)) {
                    descriptor.containerType = ContainerType.LIST;

                } else if (Set.class.isAssignableFrom(clazz)) {
                    descriptor.containerType = ContainerType.SET;

                } else {
                    descriptor.containerType = ContainerType.LIST;
                }

            } catch (Exception e) {

            }
        } else {
            return fromClass(clazz);
        }

        return descriptor;
    }

    public static enum ContainerType {
        ARRAY, LIST, SET;
    }

}
