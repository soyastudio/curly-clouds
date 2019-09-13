package soya.framework.curly.processors;

import soya.framework.commons.io.IOUtils;
import soya.framework.curly.Processor;
import soya.framework.curly.Session;
import soya.framework.curly.support.JsonData;

import java.io.IOException;
import java.io.InputStream;

public class ResourceReaderProcessor implements Processor {
    private String url;

    public ResourceReaderProcessor(String url) {
        this.url = url;
    }

    @Override
    public void process(Session session) throws IOException {
        // TODO based on different protocol
        InputStream inStream = getClassLoader().getResourceAsStream(url);

        String json = IOUtils.toString(inStream);
        session.updateState(JsonData.fromJson(json));
    }

    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(classLoader == null) {
            classLoader = getClass().getClassLoader();
        }

        return classLoader;
    }
}
