package es.urjc.etsii.grafo.iudex.entities;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
public class ContestProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    Contest contest;

    @ManyToOne
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

    public ContestProblem(Long id, Contest contest, Problem problem, LocalDateTime registeredAt) {
        this.id = id;
        this.contest = contest;
        this.problem = problem;
        this.registeredAt = registeredAt;
    }
}