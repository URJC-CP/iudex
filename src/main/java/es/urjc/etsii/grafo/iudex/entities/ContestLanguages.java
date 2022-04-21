package es.urjc.etsii.grafo.iudex.entities;

import java.time.LocalDateTime;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class ContestLanguages {

    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    Contest contest;

    @ManyToOne
    @JoinColumn(name = "language_id")
    Language lenguajes;

    LocalDateTime registeredAt;

    int grade;

    public ContestLanguages(Long id, Contest contest, Language lenguajes, LocalDateTime registeredAt, int grade) {
        this.id = id;
        this.contest = contest;
        this.lenguajes = lenguajes;
        this.registeredAt = registeredAt;
        this.grade = grade;
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

    public Language getLenguajes() {
        return lenguajes;
    }

    public void setLenguajes(Language lenguajes) {
        this.lenguajes = lenguajes;
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
        result = prime * result + ((contest == null) ? 0 : contest.hashCode());
        result = prime * result + grade;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lenguajes == null) ? 0 : lenguajes.hashCode());
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
        ContestLanguages other = (ContestLanguages) obj;
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
        if (lenguajes == null) {
            if (other.lenguajes != null)
                return false;
        } else if (!lenguajes.equals(other.lenguajes))
            return false;
        if (registeredAt == null) {
            if (other.registeredAt != null)
                return false;
        } else if (!registeredAt.equals(other.registeredAt))
            return false;
        return true;
    }

    
}
