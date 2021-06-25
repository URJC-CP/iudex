package com.example.aplicacion.entities;

import com.example.aplicacion.pojos.ContestAPI;
import com.example.aplicacion.pojos.LanguageAPI;
import com.example.aplicacion.pojos.ProblemAPI;
import com.example.aplicacion.pojos.TeamAPI;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String nombreContest;
    @Lob
    private String descripcion;

    @ManyToOne
    private Team teamPropietario;
    @ManyToMany
    private Set<Problem> listaProblemas;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Language> lenguajes;

    @ManyToMany(mappedBy = "listaContestsParticipados")
    private Set<Team> listaParticipantes;
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL)
    private Set<Submission> listaSubmissions;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public Contest() {
        this.listaProblemas = new HashSet<>();
        this.listaParticipantes = new HashSet<>();
        this.listaSubmissions = new HashSet<>();
        this.lenguajes = new HashSet<>();
        this.startDateTime = LocalDateTime.now();
        this.endDateTime = startDateTime.plusDays(15);
    }

    public ContestAPI toContestAPI() {
        ContestAPI contestAPI = new ContestAPI();
        contestAPI.setId(this.id);
        contestAPI.setNombreContest(this.nombreContest);
        contestAPI.setDescripcion(this.descripcion);
        contestAPI.setTeamPropietario(this.teamPropietario.toTeamAPISimple());

        List<LanguageAPI> lenguajesAceptados = new ArrayList<>();
        for (Language lenguaje : lenguajes) {
            lenguajesAceptados.add(lenguaje.toLanguageAPISimple());
        }
        contestAPI.setLenguajesAceptados(lenguajesAceptados);

        List<ProblemAPI> listaProblemass = new ArrayList<>();
        for (Problem problem : this.listaProblemas) {
            listaProblemass.add(problem.toProblemAPISimple());
        }
        contestAPI.setListaProblemas(listaProblemass);

        List<TeamAPI> teamAPIS = new ArrayList<>();
        for (Team team : this.listaParticipantes) {
            teamAPIS.add(team.toTeamAPISimple());
        }
        contestAPI.setListaParticipantes(teamAPIS);

        contestAPI.setStartDateTime(convertLocalDateTimeToMillis(this.startDateTime));
        contestAPI.setEndDateTime(convertLocalDateTimeToMillis(this.endDateTime));
        return contestAPI;
    }

    public ContestAPI toContestAPIFull() {
        ContestAPI contestAPI = new ContestAPI();
        contestAPI.setId(this.id);
        contestAPI.setNombreContest(this.nombreContest);
        contestAPI.setDescripcion(this.descripcion);
        contestAPI.setTeamPropietario(this.teamPropietario.toTeamAPISimple());

        List<LanguageAPI> lenguajesAceptados = new ArrayList<>();
        for (Language lenguaje : lenguajes) {
            lenguajesAceptados.add(lenguaje.toLanguageAPISimple());
        }
        contestAPI.setLenguajesAceptados(lenguajesAceptados);

        List<ProblemAPI> listaProblemass = new ArrayList<>();
        for (Problem problem : this.listaProblemas) {
            listaProblemass.add(problem.toProblemAPI());
        }
        contestAPI.setListaProblemas(listaProblemass);

        List<TeamAPI> teamAPIS = new ArrayList<>();
        for (Team team : this.listaParticipantes) {
            teamAPIS.add(team.toTeamAPISimple());
        }
        contestAPI.setListaParticipantes(teamAPIS);

        contestAPI.setStartDateTime(convertLocalDateTimeToMillis(this.startDateTime));
        contestAPI.setEndDateTime(convertLocalDateTimeToMillis(this.endDateTime));
        return contestAPI;
    }

    public ContestAPI toContestAPISimple() {
        ContestAPI contestAPI = new ContestAPI();
        contestAPI.setId(this.id);
        contestAPI.setNombreContest(this.nombreContest);
        return contestAPI;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreContest() {
        return nombreContest;
    }

    public void setNombreContest(String nombreContest) {
        this.nombreContest = nombreContest;
    }

    public Team getTeamPropietario() {
        return teamPropietario;
    }

    public void setTeamPropietario(Team teamPropietario) {
        this.teamPropietario = teamPropietario;
    }

    public Set<Problem> getListaProblemas() {
        return listaProblemas;
    }

    public void setListaProblemas(Set<Problem> listaProblemas) {
        this.listaProblemas = listaProblemas;
    }

    public Set<Language> getLenguajes() {
        return lenguajes;
    }

    public void setLenguajes(Set<Language> lenguajes) {
        this.lenguajes = lenguajes;
    }

    public void addLanguage(Language language) {
        lenguajes.add(language);
    }

    public void clearLanguage() {
        lenguajes.clear();
    }

    public void removeLanguage(Language language) {
        lenguajes.remove(language);
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Set<Team> getListaParticipantes() {
        return listaParticipantes;
    }

    public void setListaParticipantes(Set<Team> listaParticipantes) {
        this.listaParticipantes = listaParticipantes;
    }

    public void addProblem(Problem problem) {
        this.listaProblemas.add(problem);
    }

    public void deleteProblem(Problem problem) {
        this.listaProblemas.remove(problem);
    }

    public void addTeam(Team team) {
        this.listaParticipantes.add(team);
    }

    public void deleteTeam(Team team) {
        this.listaParticipantes.remove(teamPropietario);
    }

    public Set<Submission> getListaSubmissions() {
        return listaSubmissions;
    }

    public void setListaSubmissions(Set<Submission> listaSubmissions) {
        this.listaSubmissions = listaSubmissions;
    }

    public void addSubmission(Submission submission) {
        this.listaSubmissions.add(submission);
    }

    public void removeSubmission(Submission submission) {
        this.listaSubmissions.remove(submission);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String description) {
        this.descripcion = description;
    }

    private long convertLocalDateTimeToMillis(LocalDateTime dateTime) {
        return dateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contest contest = (Contest) o;
        return id == contest.getId() && nombreContest.equals(contest.getNombreContest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreContest);
    }
}
