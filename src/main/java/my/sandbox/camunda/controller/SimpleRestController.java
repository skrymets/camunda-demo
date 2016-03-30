package my.sandbox.camunda.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dtv
 */
@RestController
@RequestMapping("/bpm/api")
@EnableAutoConfiguration
public class SimpleRestController {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleRestController.class);

    @Autowired
    protected RuntimeService bpmService;

    @RequestMapping(
            method = RequestMethod.GET, 
            path = "go/{process-id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @Validated
    public String go(@PathVariable("process-id") @NotNull @Size(min = 2) final String processId) {

        ProcessInstance processInstance;
        try {
            processInstance = bpmService.startProcessInstanceByKey(processId);
            return processInstance.getProcessInstanceId();
        } catch (ProcessEngineException e) {
            LOG.error(e.getLocalizedMessage());
            throw e;
        }
    }

    @ExceptionHandler(ProcessEngineException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleProcessEngineException(Throwable ex) {
        return ex.getLocalizedMessage();
    }
}
