package my.sandbox.camunda.services;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author dtv
 */
@Service("logDelegate")
public class LogDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(LogDelegate.class);

    public void log(String message) throws Exception {
        LOG.info(message);
    }
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOG.info(
                "Step: \"{}\"\n"
                + "Process: {} / {} / {}",
                execution.getCurrentActivityName(),
                execution.getProcessDefinitionId(),
                execution.getProcessInstanceId(),
                execution.getProcessBusinessKey()
        );
    }

}
