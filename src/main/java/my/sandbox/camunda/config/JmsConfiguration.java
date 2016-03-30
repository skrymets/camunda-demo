package my.sandbox.camunda.config;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

/**
 * @author dtv
 */
@Configuration
@EnableJms
public class JmsConfiguration {

    @Bean
    JmsListenerContainerFactory<?> myJmsContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        /**
         *********************************************************************************
         * FIX: This class is not trusted to be serialized as ObjectMessage payload.
         *********************************************************************************
         * Could not convert JMS message;
         * javax.jms.JMSException: Failed to build body from content.
         * Serializable class not available to broker.
         * Reason: Forbidden class my.sandbox.camunda.jms.DataRequestMessage!
         * This class is not trusted to be serialized as ObjectMessage payload.
         * Please take a look at http://activemq.apache.org/objectmessage.html for more
         * information on how to configure trusted classes.
         */
        if (connectionFactory instanceof ActiveMQConnectionFactory) {
            ((ActiveMQConnectionFactory) connectionFactory).setTrustAllPackages(true);
        }
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

}
