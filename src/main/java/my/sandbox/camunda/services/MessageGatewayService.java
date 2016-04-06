package my.sandbox.camunda.services;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.UUID;
import my.sandbox.camunda.jms.DataRequestMessage;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author dtv
 */
@Service("messageGatewayService")
public class MessageGatewayService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageGatewayService.class);

    @Autowired
    protected RuntimeService bpmService;

    @Autowired
    protected LogDelegate logDelegate;

    @Autowired
    protected JmsMessagingTemplate jmsTemplate;

    @JmsListener(destination = "demo-queue-1", containerFactory = "myJmsContainerFactory")
    @SendTo("demo-queue-2")
    public DataRequestMessage readQueue1(DataRequestMessage message) throws Exception {

        LOG.info("Read from \"demo-queue-1\" {}", "", message.getInstanceId());
        LOG.info("Modify data");
        message.setData("+++" + message.getData() + "+++");
        LOG.info("Send modified data to \"demo-queue-2\"");
        return message;

    }

    @JmsListener(destination = "demo-queue-2", containerFactory = "myJmsContainerFactory")
    public void readQueue2(Message<DataRequestMessage> message) throws Exception {

        final String MSG_CONTINUE_MAIN = "continueExecutionByEventMessage";

        DataRequestMessage payload = message.getPayload();

        LOG.info("Read from \"demo-queue-2\". Process ID: {}", payload.getInstanceId());
        
        bpmService
                .createMessageCorrelation(MSG_CONTINUE_MAIN)
                .processInstanceId(payload.getInstanceId())
                .setVariable("businessUUID", UUID.randomUUID().toString())
                .correlate();
        
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//        Execution awaitingExecution = bpmService
//                .createExecutionQuery()
//                .messageEventSubscriptionName(MSG_CONTINUE_MAIN)
//                .processInstanceId(payload.getInstanceId())
//                .singleResult();
//        if (awaitingExecution == null) {
//            LOG.warn("There is no execution in process {} that is awaiting for '{}' message.", payload.getInstanceId(), MSG_CONTINUE_MAIN);
//        } else {
//            LOG.info("Notify process {} with message {}", payload.getInstanceId(), payload.getData());
//
//            bpmService.messageEventReceived(
//                    MSG_CONTINUE_MAIN,
//                    awaitingExecution.getId(),
//                    Collections.singletonMap("businessUUID", message.getHeaders().get("businessUUID"))
//            );
//        }
    }

    @JmsListener(destination = "demo-queue-3", containerFactory = "myJmsContainerFactory")
    public void readQueue3(Message<DataRequestMessage> message) throws Exception {
        DataRequestMessage payload = message.getPayload();
        LOG.info("Read from \"demo-queue-3\". Process ID: {}", payload.getInstanceId());
    }

    /**
     * Sends a message to a JMS queue
     *
     * @param queueName   destination queue name
     * @param messageData message data
     * @param execution   BPM execution context
     *
     * @return a unique message id
     */
    public String sendMessage(String queueName, String messageData, DelegateExecution execution) {

        try {
            logDelegate.execute(execution);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage());
        }

        LOG.info("Sending \"{}\" to queue \"{}\"", messageData, queueName);

        DataRequestMessage payload = new DataRequestMessage()
                .setData(messageData)
                .setInstanceId(execution.getProcessInstanceId());

        final Message<DataRequestMessage> message = MessageBuilder
                .withPayload(payload)
                .build();

        jmsTemplate.send(queueName, message);

        return UUID.randomUUID().toString();
    }

}
