package com.example.aplicacion.Entities;

import javax.persistence.*;

@Entity
public class SubmissionProblemValidator{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String expectedSolution;
    @OneToOne(cascade = CascadeType.ALL)
    private Submission submission;

    public SubmissionProblemValidator(){}


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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
