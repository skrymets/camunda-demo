package my.sandbox.camunda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author dtv
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
        SpringApplication.run(Application.class, args);
    }
}
