package soya.framework.curly.support;

import com.google.gson.JsonElement;
import soya.framework.curly.JsonCompatible;

public interface GsonCompatible extends JsonCompatible {
    JsonElement getAsJsonElement();
}
