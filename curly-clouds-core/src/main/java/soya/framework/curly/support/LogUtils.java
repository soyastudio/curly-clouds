package soya.framework.curly.support;

import java.util.logging.Logger;

public class LogUtils {
    public static  boolean enabled = true;

    public static void log(String message, Class<?> cls, String indent) {
        if(enabled) {
            Logger logger = Logger.getLogger(cls.getName());
            logger.info(indent + message);
        }
    }
}
