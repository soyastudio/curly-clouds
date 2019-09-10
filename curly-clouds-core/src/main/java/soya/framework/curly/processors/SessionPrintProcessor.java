package soya.framework.curly.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import soya.framework.curly.DataObject;
import soya.framework.curly.Processor;
import soya.framework.curly.Session;
import soya.framework.curly.util.GsonUtils;

import java.util.logging.Logger;

public final class SessionPrintProcessor implements Processor {

    private static Logger logger = Logger.getLogger(SessionPrintProcessor.class.getName());

    @Override
    public void process(Session session) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uri", session.getUri());
        jsonObject.addProperty("id", session.getId());

        jsonObject.add("IN", GsonUtils.toJsonElement(session.getInvocation()));

        DataObject state = session.getCurrentState();
        jsonObject.add("OUT", GsonUtils.toJsonElement(state));

        logger.info("SESSION: \n" + gson.toJson(jsonObject));

    }
}
