package com.example.aplicacion.Entities;

public interface ProblemData {
    String getName();
    void setName(String name);
    String getText();
    void setText(String text);
    ProblemDataType getType();
    int getTypeNumber();
    String getTypeString();
}
