package soya.framework.curly;

import java.util.Set;

public interface DispatchRegistration {
    String getName();

    Set<String> schemas();

    Operation getProcessor(String uri);
}
