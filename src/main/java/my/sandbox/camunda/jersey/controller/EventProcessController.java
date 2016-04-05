package my.sandbox.camunda.jersey.controller;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.exception.NullValueException;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dtv
 */
@Component
@Path("/ep")
public class EventProcessController {

    private static final Logger LOG = LoggerFactory.getLogger(EventProcessController.class);

    protected static final String EVENT_PROCESS_CONTINUE_MSG = "eventProcessContinueMsg";

    @Autowired
    protected RuntimeService bpmService;

    @GET
    @Path("/hc")
    @Produces(MediaType.APPLICATION_JSON)
    public String serviceHealthCheck() {
        return "OK";
    }

    @GET
    @Path("go/{process-id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response go(@PathParam("process-id") @NotNull @Size(min = 2) final String processId) {

        ProcessInstance processInstance;
        try {
            processInstance = bpmService.startProcessInstanceByKey(processId);
            return Response.status(Response.Status.CREATED).entity(processInstance.getProcessInstanceId()).build();
        } catch (ProcessEngineException e) {
            LOG.error(e.getLocalizedMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getLocalizedMessage()).build();
        }
    }

    @GET
    @Path("/resume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resume() {
        List<EventSubscription> eventSubscriptions = bpmService
                .createEventSubscriptionQuery()
                .eventName(EVENT_PROCESS_CONTINUE_MSG)
                .list();
        if (eventSubscriptions.isEmpty()) {
            LOG.info("There are no subscriptions for \"{}\"", EVENT_PROCESS_CONTINUE_MSG);
            return Response.status(Response.Status.OK).entity("----").build();
        }

        eventSubscriptions.stream().forEach((subscription) -> {
            bpmService.messageEventReceived(EVENT_PROCESS_CONTINUE_MSG, subscription.getExecutionId());
            LOG.info("Notify {}@{} with {}",
                    subscription.getActivityId(),
                    subscription.getProcessInstanceId(),
                    EVENT_PROCESS_CONTINUE_MSG
            );
        });
        return Response
                .status(Response.Status.OK)
                .entity("Notified " + eventSubscriptions.size() + " activities")
                .build();
    }

}
