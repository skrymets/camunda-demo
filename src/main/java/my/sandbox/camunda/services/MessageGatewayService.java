package my.sandbox.camunda.services;

import my.sandbox.camunda.jms.DataRequestMessage;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
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
    protected JmsMessagingTemplate jmsTemplate;

    @JmsListener(destination = "demo-queue-1", containerFactory = "myJmsContainerFactory")
    public void readQueue1(DataRequestMessage message) throws Exception {
        LOG.info("Read from \"demo-queue-1\" {}", "", message.getInstanceId());

        Thread.sleep(2000);
        
        bpmService
                .createMessageCorrelation("continueExecutionByEventMessage")
                .processInstanceId(message.getInstanceId())
                .correlate();

    }

    @JmsListener(destination = "demo-queue-3", containerFactory = "myJmsContainerFactory")
    public void readQueue3(DataRequestMessage message) throws Exception {
        LOG.info("Read from \"demo-queue-3\" {}", message.getInstanceId());

//        bpmService
//                .createMessageCorrelation("continueExecutionByEventMessage")
//                .processInstanceId(message.getProcessInstanceId())
//                .correlate();
    }

    public Object sendMessage(String queueName, String messageData, DelegateExecution execution) {

        LOG.info("Sending \"{}\" to \"{}\" from \"[{}]:{}\"", messageData, queueName,
                execution.getActivityInstanceId(),
                execution.getCurrentActivityName()
        );

        DataRequestMessage message = new DataRequestMessage()
                .setData(messageData)
                .setInstanceId(execution.getActivityInstanceId());
        jmsTemplate.send(queueName, MessageBuilder.withPayload(message).build());

        return true;
    }

}
