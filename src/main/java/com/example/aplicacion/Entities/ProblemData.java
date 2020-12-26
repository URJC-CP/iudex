package com.example.aplicacion.Entities;

import com.example.aplicacion.Pojos.InNOutAPI;
import com.example.aplicacion.Pojos.ProblemDataType;

import javax.persistence.*;
import java.time.Instant;

//tupla q contiene el nombre y texto de una answer
@Entity
public class ProblemData {
    private ProblemDataType type;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private long timestamp = Instant.now().toEpochMilli();

    //@ManyToOne
    //private Problem problem;

    @Lob
    private String text;

    public ProblemData() {
    }

    public ProblemData(String name, String text, ProblemDataType type) {
        this.name = name;
        this.text = text;
        this.type = type;
    }

    public ProblemData(long id, String name, String text, ProblemDataType type) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.type = type;
    }

    public InNOutAPI toInNOutAPI() {
        InNOutAPI inNOutAPI = new InNOutAPI();
        inNOutAPI.setId(this.id);
        inNOutAPI.setName(this.name);
        inNOutAPI.setText(this.text);
        inNOutAPI.setTimestamp(this.timestamp);
        return inNOutAPI;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    //public Problem getProblem() {
    //    return problem;
    //}

    //public void setProblem(Problem problem) {
    //    this.problem = problem;
    //}

    public String toString() {
        return this.name + this.text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ProblemDataType getType() {
        return type;
    }

    public int getTypeNumber() {
        return type.ordinal();
    }

    public String getTypeString() {
        return type.name();
    }
}
