package my.sandbox.camunda.jms;

import java.io.Serializable;

/**
 * @author dtv
 */
public class DataRequestMessage implements Serializable {

    private static final long serialVersionUID = -3821300948973997838L;

    private String processInstanceId;

    public DataRequestMessage() {
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public DataRequestMessage setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

}
