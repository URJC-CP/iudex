package com.example.aplicacion.pojos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SampleAPI {
    private long id;
    private String name;
    private String inputText;
    private String outputText;
    private long timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String text) {
        this.inputText = text;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String text) {
        this.outputText = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
