package my.sandbox.camunda.config;

import javax.annotation.PostConstruct;
import my.sandbox.camunda.jersey.controller.EventProcessController;
import org.camunda.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @author dtv
 */
@Configuration
public class JerseyRestConfiguration extends CamundaJerseyResourceConfig {

    public JerseyRestConfiguration() {
    }

    @PostConstruct
    public void configure() {
        register(EventProcessController.class);
    }

}
