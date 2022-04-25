package es.urjc.etsii.grafo.iudex.entities;

import java.time.LocalDateTime;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class TeamUser {

    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team teams;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    LocalDateTime registeredAt;

    int grade;

    public TeamUser(Long id, Team teams, User user, LocalDateTime registeredAt, int grade) {
        this.id = id;
        this.teams = teams;
        this.user = user;
        this.registeredAt = registeredAt;
        this.grade = grade;
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

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + grade;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((registeredAt == null) ? 0 : registeredAt.hashCode());
        result = prime * result + ((teams == null) ? 0 : teams.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        TeamUser other = (TeamUser) obj;
        if (grade != other.grade)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (registeredAt == null) {
            if (other.registeredAt != null)
                return false;
        } else if (!registeredAt.equals(other.registeredAt))
            return false;
        if (teams == null) {
            if (other.teams != null)
                return false;
        } else if (!teams.equals(other.teams))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }
    
    
}
