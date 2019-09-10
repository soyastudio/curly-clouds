package soya.framework.curly.rest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import soya.framework.curly.DispatchExecutor;
import soya.framework.curly.support.DispatchServiceSingleton;
import soya.framework.curly.support.UriDispatchService;

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
            builder.setExecutor(executor);
        } catch (BeansException e) {
            // do nothing
        }

        // Rest Dispatch Service:
        RestDispatchService restDispatchService = new RestDispatchService();
        restDispatchService.registerSubjects(getClasses().toArray(new Class<?>[getClasses().size()]));
        restDispatchService.registerProcessors(applicationContext.getBeansOfType(RestOperation.class));
        builder.register(restDispatchService);

        // Other Dispatch Services:
        applicationContext.getBeansOfType(UriDispatchService.class).entrySet().forEach(e -> {
            builder.register(e.getValue());
            logger.info("register dispatch service: " + e.getValue().getClass().getName());
        });

        builder.build();

    }
}
