package es.urjc.etsii.grafo.iudex.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    LocalDateTime registeredAt;

    public TeamUser(Team team, User user, LocalDateTime registeredAt) {
        this.team = team;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team teams) {
        this.team = teams;
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
        return Objects.equals(getTeam(), teamUser.getTeam()) && Objects.equals(getUser(), teamUser.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeam(), getUser());
    }
}
