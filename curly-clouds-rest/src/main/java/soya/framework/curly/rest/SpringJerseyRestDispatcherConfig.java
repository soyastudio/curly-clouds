package soya.framework.curly.rest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import soya.framework.curly.DispatchExecutor;
import soya.framework.curly.Dispatcher;
import soya.framework.curly.support.DispatchServiceSingleton;
import soya.framework.curly.support.DispatchServiceSupport;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class SpringJerseyRestDispatcherConfig extends JerseyRestDispatcherConfig implements ApplicationContextAware {
    static Logger logger = Logger.getLogger(SpringJerseyRestDispatcherConfig.class.getName());

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected void init() {
        DispatchServiceSingleton.DispatchServiceBuilder builder = DispatchServiceSingleton.builder();
        try {
            DispatchExecutor executor = applicationContext.getBean(DispatchExecutor.class);
            builder.setExecutor(executor).setDeserializer(new RestSessionDeserializer());

        } catch (BeansException e) {
            // do nothing
        }

        // Other Dispatch Services:
        applicationContext.getBeansOfType(DispatchServiceSupport.class).entrySet().forEach(e -> {
            builder.registerDispatchService(e.getValue());
            logger.info("register dispatch service: " + e.getValue().getClass().getName());
        });

        RestSubjectRegistration subjectRegistration = new RestSubjectRegistration();
        Set<Class<?>> set = new HashSet<>();
        Class<?>[] classes = getClasses().toArray(new Class[getClasses().size()]);
        subjectRegistration.registerSubjects(classes);
        builder.registerSubject(subjectRegistration);

        builder.setCallbackFactory(new RestMethodCallbackFactory());

        builder.build();

    }
}
