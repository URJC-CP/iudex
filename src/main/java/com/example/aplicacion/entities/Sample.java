package com.example.aplicacion.entities;

import com.example.aplicacion.pojos.SampleAPI;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

//tupla q contiene el nombre y texto de una answer
@Entity
public class Sample {
    private boolean isPublic;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private long timestamp = Instant.now().toEpochMilli();

    @Lob
    private String inputText;
    @Lob
    private String outputText;

    public Sample() {
    }

    public Sample(String name, String inputText, String outputText, boolean isPublic) {
        this.name = name;
        this.inputText = inputText;
        this.outputText = outputText;
        this.isPublic = isPublic;
    }

    public Sample(long id, String name, String inputText, String outputText, boolean isPublic) {
        this(name, inputText, outputText, isPublic);
        this.id = id;
    }

    public SampleAPI toSampleAPI() {
        SampleAPI sampleAPI = new SampleAPI();
        sampleAPI.setId(this.id);
        sampleAPI.setName(this.name);
        sampleAPI.setInputText(this.inputText);
        sampleAPI.setOutputText(this.outputText);
        sampleAPI.setTimestamp(this.timestamp);
        return sampleAPI;
    }

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

    public String toString() {
        return this.name + this.inputText + this.outputText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sample that = (Sample) o;
        return id == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
