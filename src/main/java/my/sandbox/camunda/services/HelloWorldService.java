package my.sandbox.camunda.services;

import javax.annotation.PostConstruct;
import javax.jms.ObjectMessage;
import my.sandbox.camunda.jms.DataRequestMessage;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author dtv
 */
@Service
public class HelloWorldService implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldService.class);

    protected static final String JMS_QUEUE_CAMUNDA_DEMO = "camunda-demo-1";

    @Autowired
    protected RuntimeService bpmService;

    @Autowired
    protected JmsMessagingTemplate jmsTemplate;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOG.info("BPM: Hello!");
    }

    public void requestData(DelegateExecution execution) throws Exception {
        final String processInstanceId = execution.getProcessInstanceId();
        LOG.info("BPM: request data for {}", processInstanceId);
        prepareData(processInstanceId);
    }

    public void prepareData(String processInstanceId) throws Exception {
        DataRequestMessage message = new DataRequestMessage().setInstanceId(processInstanceId);
        jmsTemplate.send(JMS_QUEUE_CAMUNDA_DEMO, MessageBuilder.withPayload(message).build());
    }

    @JmsListener(destination = JMS_QUEUE_CAMUNDA_DEMO, containerFactory = "myJmsContainerFactory")
    public void readDataResponse(DataRequestMessage message) throws Exception {
        LOG.info("A message received for process {}", message.getInstanceId());
    }

    @PostConstruct
    public void startProcess() {
        // bpmService.startProcessInstanceById("helloWorldProcess");
    }

}
