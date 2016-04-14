package my.sandbox.camunda.jersey.controller;

import java.util.Collections;
import static java.util.Collections.singletonMap;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import my.sandbox.camunda.data.model.BusinessData;
import my.sandbox.camunda.data.repository.BusinessDataRepository;
import my.sandbox.camunda.jersey.controller.dto.StringValue;
import my.sandbox.camunda.services.MessageGatewayService;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
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

    @Autowired
    protected RuntimeService bpmService;
    
    @Autowired
    protected BusinessDataRepository dataRepository;
    
    @Autowired
    protected MessageGatewayService messageGatewayService;

    @POST
    @Path("/business_entity")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewBusinessEntity() {
        String data = UUID.randomUUID().toString();
        dataRepository.save(new BusinessData(data));
        return Response.ok(new StringValue(data)).build();
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

    protected static final String REST_EVENT_MSG = "continueExecutionByRestMessage";
    
    @GET
    @Path("/resume/{msg}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resume(@PathParam("msg") @DefaultValue(REST_EVENT_MSG) String msg) {
        int eventSubscriptions = messageGatewayService.processRestCall(msg);
        return Response
                .status(Response.Status.OK)
                .entity("Notified " + eventSubscriptions + " activities")
                .build();
    }
    
    
    @GET
    @Path("/mainProcess2/{msg}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response mainProcess2(@PathParam("msg") @DefaultValue(REST_EVENT_MSG) String msg) {
        ProcessInstance processInstance = bpmService
                .startProcessInstanceByMessage("SimpleProcessMessage", singletonMap("startupValue", msg));
        return Response.ok().entity(processInstance.getProcessInstanceId()).build();
    }

}
