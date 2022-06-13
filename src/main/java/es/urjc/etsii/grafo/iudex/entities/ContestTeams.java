package es.urjc.etsii.grafo.iudex.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.*;

@Entity
public class ContestTeams {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    Contest contest;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team teams;

    LocalDateTime registeredAt;

    public ContestTeams() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public Team getTeams() {
        return teams;
    }

    public void setTeams(Team teams) {
        this.teams = teams;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public ContestTeams(Contest contest, Team teams, LocalDateTime registeredAt, int grade) {
        this.contest = contest;
        this.teams = teams;
        this.registeredAt = registeredAt;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContestTeams)) return false;
        ContestTeams that = (ContestTeams) o;
        return getContest().equals(that.getContest()) && getTeams().equals(that.getTeams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContest(), getTeams());
    }
}
