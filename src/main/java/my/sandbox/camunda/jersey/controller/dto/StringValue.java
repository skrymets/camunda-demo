package my.sandbox.camunda.jersey.controller.dto;

import java.io.Serializable;

public class StringValue implements Serializable {

    private static final long serialVersionUID = -705917575353926862L;

    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
