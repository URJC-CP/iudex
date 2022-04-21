package es.urjc.etsii.grafo.iudex.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ContestProblem {

    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    Contest contest;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    Problem problem;

    LocalDateTime registeredAt;

    int grade;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contest == null) ? 0 : contest.hashCode());
        result = prime * result + grade;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((problem == null) ? 0 : problem.hashCode());
        result = prime * result + ((registeredAt == null) ? 0 : registeredAt.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContestProblem other = (ContestProblem) obj;
        if (contest == null) {
            if (other.contest != null)
                return false;
        } else if (!contest.equals(other.contest))
            return false;
        if (grade != other.grade)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (problem == null) {
            if (other.problem != null)
                return false;
        } else if (!problem.equals(other.problem))
            return false;
        if (registeredAt == null) {
            if (other.registeredAt != null)
                return false;
        } else if (!registeredAt.equals(other.registeredAt))
            return false;
        return true;
    }

    public ContestProblem(Long id, Contest contest, Problem problem, LocalDateTime registeredAt, int grade) {
        this.id = id;
        this.contest = contest;
        this.problem = problem;
        this.registeredAt = registeredAt;
        this.grade = grade;
    }
}