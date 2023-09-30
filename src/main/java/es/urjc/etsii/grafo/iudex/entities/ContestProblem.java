package es.urjc.etsii.grafo.iudex.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ContestProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contest_id")
    Contest contest;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_id")
    Problem problem;

    LocalDateTime registeredAt;

    public ContestProblem() {

    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ContestProblem(Contest contest, Problem problem, LocalDateTime registeredAt) {
        this.contest = contest;
        this.problem = problem;
        this.registeredAt = registeredAt;
    }
}