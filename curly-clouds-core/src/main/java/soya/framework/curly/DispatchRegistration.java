package soya.framework.curly;

import java.util.Set;

public interface DispatchRegistration {
    String getName();

    Set<String> schemas();

    Set<String> available();

    boolean contains(String uri);

    DispatchMethod getDispatchMethod(String uri);

    Operation getProcessor(String uri);
}
