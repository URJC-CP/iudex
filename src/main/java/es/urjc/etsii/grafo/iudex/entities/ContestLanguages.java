package es.urjc.etsii.grafo.iudex.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.*;

@Entity
public class  ContestLanguages {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    Contest contest;

    @ManyToOne
    @JoinColumn(name = "language_id")
    Language lenguajes;

    LocalDateTime registeredAt;

    public ContestLanguages(Contest contest, Language lenguajes, LocalDateTime registeredAt) {
        this.contest = contest;
        this.lenguajes = lenguajes;
        this.registeredAt = registeredAt;
    }
    public ContestLanguages() {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContestLanguages)) return false;
        ContestLanguages that = (ContestLanguages) o;
        return Objects.equals(getContest(), that.getContest()) && Objects.equals(getLenguajes(), that.getLenguajes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContest(), getLenguajes());
    }
}
