package my.sandbox.camunda.services;

import java.util.Collection;
import java.util.List;

import java.util.UUID;
import java.util.Collections;
import java.util.stream.Stream;


import my.sandbox.camunda.jms.DataRequestMessage;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dtv
 */
@Service("messageGatewayService")
//@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class MessageGatewayService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageGatewayService.class);

    @Autowired
    protected RuntimeService bpmService;

    @Autowired
    protected LogDelegate logDelegate;

    @Autowired
    protected JmsMessagingTemplate jmsTemplate;

    @JmsListener(destination = "demo-queue-1", containerFactory = "myJmsContainerFactory")
    // @SendTo("demo-queue-2")
    public void /* DataRequestMessage */ readQueue1(DataRequestMessage message) throws Exception {

        LOG.info("Read from 'demo-queue-1' {}", "", message.getInstanceId());
        LOG.info("Modify data");
        message.setData("+++" + message.getData() + "+++");
        LOG.info("Send modified data to 'demo-queue-2'");
        
        forwardToQueue2(message);
        
        // return message;
    }

    @JmsListener(destination = "demo-queue-2", containerFactory = "myJmsContainerFactory")
    public void readQueue2(Message<DataRequestMessage> message) throws Exception {

        final String MSG_CONTINUE_MAIN = "continueExecutionByEventMessage";

        DataRequestMessage payload = message.getPayload();
        final String instanceId = payload.getInstanceId();

        LOG.info("Read from 'demo-queue-2'. Process ID: {}", instanceId);

        Collection<EventSubscription> es = bpmService.createEventSubscriptionQuery()
                .eventName(MSG_CONTINUE_MAIN)
                // .processInstanceId(instanceId)
                .list();
        if (es == null || es.isEmpty()) {
            // LOG.warn("There is no process with id {} that is awaiting for '{}' message.", instanceId, MSG_CONTINUE_MAIN);
            LOG.warn("There is no process that is awaiting for '{}' message.", MSG_CONTINUE_MAIN);
        } else {
            es.stream().forEach(subscription -> {
                LOG.info("Found subscription: {}", subscription);
                // bpmService.messageEventReceived(MSG_CONTINUE_MAIN, subscription.getExecutionId());
            });
        }
                
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//        bpmService
//                .createMessageCorrelation(MSG_CONTINUE_MAIN)
//                .processInstanceId(payload.getInstanceId())
//                .setVariable("businessUUID", UUID.randomUUID().toString())
//                .correlate();
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//        Execution awaitingExecution = bpmService
//                .createExecutionQuery()
//                .messageEventSubscriptionName(MSG_CONTINUE_MAIN)
//                .processInstanceId(instanceId)
//                .singleResult();
//
//        if (awaitingExecution == null) {
//            LOG.warn("There is no execution in process {} that is awaiting for '{}' message.", instanceId, MSG_CONTINUE_MAIN);
//        } else {
//            LOG.info("Notify process {} with message {}", instanceId, payload.getData());
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
        LOG.info("Read from 'demo-queue-3'. Process ID: {}", payload.getInstanceId());
    }

    public int processRestCall(String messageName) {
        List<EventSubscription> eventSubscriptions = bpmService
                .createEventSubscriptionQuery()
                .eventName(messageName)
                .list();

        if (eventSubscriptions.isEmpty()) {
            LOG.info("There are no subscriptions for '{}'", messageName);
            return 0;
        }

        eventSubscriptions.stream().forEach((subscription) -> {
            bpmService.messageEventReceived(messageName, subscription.getExecutionId());
            LOG.info("Notify {}@{} with {} message",
                    subscription.getActivityId(),
                    subscription.getProcessInstanceId(),
                    messageName
            );
        });

        return eventSubscriptions.size();
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

        LOG.info("Sending '{}' to queue '{}'", messageData, queueName);

        DataRequestMessage payload = new DataRequestMessage()
                .setData(messageData)
                .setInstanceId(execution.getProcessInstanceId());

        final Message<DataRequestMessage> message = MessageBuilder
                .withPayload(payload)
                .build();

        jmsTemplate.send(queueName, message);

        return UUID.randomUUID().toString();
    }

    private void forwardToQueue2(DataRequestMessage drm) {
        
        LOG.info("Forwarding message: 'queue-1' --> 'queue-2': {}", drm.getData());
        
        final Message<DataRequestMessage> message = MessageBuilder
                .withPayload(drm)
                .build();

        jmsTemplate.send("demo-queue-2", message);
        
    }

}
