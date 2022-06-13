package es.urjc.etsii.grafo.iudex.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.*;

@Entity
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team teams;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    LocalDateTime registeredAt;

    public TeamUser(Long id, Team teams, User user, LocalDateTime registeredAt, int grade) {
        this.id = id;
        this.teams = teams;
        this.user = user;
        this.registeredAt = registeredAt;
    }

    public TeamUser(){

    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamUser)) return false;
        TeamUser teamUser = (TeamUser) o;
        return Objects.equals(getTeams(), teamUser.getTeams()) && Objects.equals(getUser(), teamUser.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeams(), getUser());
    }
}
