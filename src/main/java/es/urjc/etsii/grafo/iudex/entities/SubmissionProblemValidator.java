package es.urjc.etsii.grafo.iudex.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class SubmissionProblemValidator {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String expectedSolution;
    @OneToOne(cascade = CascadeType.ALL)
    private Submission submission;

    public SubmissionProblemValidator() {
        //empty constructor
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionProblemValidator that = (SubmissionProblemValidator) o;
        return id == that.id && submission.equals(that.submission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, submission);
    }
}
