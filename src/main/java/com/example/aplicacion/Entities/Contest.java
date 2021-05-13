package com.example.aplicacion.Entities;

import com.example.aplicacion.Pojos.ContestAPI;
import com.example.aplicacion.Pojos.LanguageAPI;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Pojos.TeamAPI;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<Problem> listaProblemas;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Language> lenguajes;

    @ManyToMany(mappedBy = "listaContestsParticipados")
    private List<Team> listaParticipantes;
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL)
    private List<Submission> listaSubmissions;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public Contest() {
        this.listaProblemas = new ArrayList<>();
        this.listaParticipantes = new ArrayList<>();
        this.listaSubmissions = new ArrayList<>();
        this.listaProblemas = new ArrayList<>();
        this.lenguajes = new ArrayList<>();
        this.startDateTime = LocalDateTime.now();
        this.endDateTime = startDateTime.plusDays(1);
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
        contestAPI.setStartDateTime(this.startDateTime);
        contestAPI.setEndDateTime(this.endDateTime);
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
        contestAPI.setStartDateTime(this.startDateTime);
        contestAPI.setEndDateTime(this.endDateTime);
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

    public List<Problem> getListaProblemas() {
        return listaProblemas;
    }

    public void setListaProblemas(List<Problem> listaProblemas) {
        this.listaProblemas = listaProblemas;
    }

    public List<Language> getLenguajes() {
        return lenguajes;
    }

    public void setLenguajes(List<Language> lenguajes) {
        this.lenguajes = lenguajes;
    }

    public void addLanguage(Language language) {
        if (!lenguajes.contains(language)) {
            lenguajes.add(language);
        }
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

    public List<Team> getListaParticipantes() {
        return listaParticipantes;
    }

    public void setListaParticipantes(List<Team> listaParticipantes) {
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

    public List<Submission> getListaSubmissions() {
        return listaSubmissions;
    }

    public void setListaSubmissions(List<Submission> listaSubmissions) {
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
