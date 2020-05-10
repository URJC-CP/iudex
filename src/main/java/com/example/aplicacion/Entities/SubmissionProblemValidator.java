package com.example.aplicacion.Entities;

import javax.persistence.*;

@Entity
public class SubmissionProblemValidator{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String expectedSolution;
    @OneToOne
    private Submission submission;

    public SubmissionProblemValidator(){}

    public SubmissionProblemValidator(String codigo, Language language, String filename, String expectedSolution){
        submission = new Submission(codigo,language, filename);
        this.expectedSolution = expectedSolution;
    }

    public String getExpectedSolution() {
        return expectedSolution;
    }

    public void setExpectedSolution(String expectedSolution) {
        this.expectedSolution = expectedSolution;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }
}
