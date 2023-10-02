package es.urjc.etsii.grafo.iudex.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class TeamsProblems {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team teams;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    Problem problem;

    LocalDateTime registeredAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeams() {
        return teams;
    }

    public void setTeams(Team teams) {
        this.teams = teams;
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

    public TeamsProblems(Team teams, Problem problem, LocalDateTime registeredAt) {
        this.teams = teams;
        this.problem = problem;
        this.registeredAt = registeredAt;
    }

    public TeamsProblems(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamsProblems)) return false;
        TeamsProblems that = (TeamsProblems) o;
        return Objects.equals(getTeams(), that.getTeams()) && Objects.equals(getProblem(), that.getProblem());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeams(), getProblem());
    }

}
