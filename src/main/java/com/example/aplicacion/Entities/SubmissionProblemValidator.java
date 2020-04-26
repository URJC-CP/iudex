package com.example.aplicacion.Entities;

import javax.persistence.Entity;

@Entity
public class SubmissionProblemValidator extends Submission{
    private String expectedSolution;

    public SubmissionProblemValidator(){}

    public SubmissionProblemValidator(String codigo, Language language, String filename, String expectedSolution){
        super(codigo,language, filename);
        this.expectedSolution = expectedSolution;
    }


    public String getExpectedSolution() {
        return expectedSolution;
    }

    public void setExpectedSolution(String expectedSolution) {
        this.expectedSolution = expectedSolution;
    }
}
