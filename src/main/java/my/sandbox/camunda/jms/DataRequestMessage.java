package my.sandbox.camunda.jms;

import java.io.Serializable;

/**
 * @author dtv
 */
public class DataRequestMessage implements Serializable {

    private static final long serialVersionUID = -3821300948973997838L;

    private String instanceId;

    private String data;

    public DataRequestMessage() {
    }

    public String getInstanceId() {
        return instanceId;
    }

    public DataRequestMessage setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public String getData() {
        return data;
    }

    public DataRequestMessage setData(String data) {
        this.data = data;
        return this;
    }

}
