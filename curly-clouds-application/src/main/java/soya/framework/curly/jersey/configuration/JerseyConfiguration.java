package soya.framework.curly.jersey.configuration;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Swagger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import soya.framework.curly.DispatchExecutor;
import soya.framework.curly.rest.SpringJerseyRestDispatcherConfig;
import soya.framework.curly.support.DirectDispatchService;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Configuration
@ApplicationPath("/api")
public class JerseyConfiguration extends SpringJerseyRestDispatcherConfig {
    private Swagger swagger;
    public JerseyConfiguration() {
        packages("soya.framework.curly.jersey.api");
        this.swagger = swaggerConfig();
    }

    @Bean
    public DispatchExecutor restDispatchExecutor() {
        return new DispatchExecutor(3, 30, 60, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    }

    @Bean
    public DirectDispatchService directDispatchService() {
        return new DirectDispatchService();
    }

    public Swagger getSwagger() {
        return swagger;
    }

    @PostConstruct
    public void init() {
       super.init();
    }

    private Swagger swaggerConfig() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig swaggerConfigBean = new BeanConfig();
        swaggerConfigBean.setConfigId("soya-curly-clouds");
        swaggerConfigBean.setTitle("Soya Curly Clouds");
        //swaggerConfigBean.setVersion("v1");
        swaggerConfigBean.setContact("wen_qun@hotmail.com");
        swaggerConfigBean.setSchemes(new String[]{"http"});
        swaggerConfigBean.setBasePath("/api");
        swaggerConfigBean.setResourcePackage("soya.framework.curly.jersey.api");
        swaggerConfigBean.setPrettyPrint(true);
        swaggerConfigBean.setScan(true);

        return swaggerConfigBean.getSwagger();
    }

}
