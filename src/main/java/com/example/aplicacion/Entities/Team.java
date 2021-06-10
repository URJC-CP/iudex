package com.example.aplicacion.Entities;

import com.example.aplicacion.Pojos.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

//CLASE donde se guarda la informacion referente a los equipos
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String nombreEquipo;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "equiposParticipantes")
    private Set<User> participantes;
    private boolean esUser;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<Submission> listaDeSubmissions;
    @OneToMany(mappedBy = "equipoPropietario")
    private Set<Problem> listaProblemasCreados;
    @ManyToMany
    private Set<Problem> listaProblemasParticipados;
    @ManyToMany
    private Set<Contest> listaContestsParticipados;
    @OneToMany(mappedBy = "teamPropietario")
    private Set<Contest> listaContestsCreados;
    private long timestamp = Instant.now().toEpochMilli();

    public Team() {
        this(null);
    }

    public Team(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
        this.participantes = new HashSet<>();
        this.listaDeSubmissions = new HashSet<>();
        this.listaProblemasCreados = new HashSet<>();
        this.listaProblemasParticipados = new HashSet<>();
        this.listaContestsCreados = new HashSet<>();
        this.listaContestsParticipados = new HashSet<>();
    }

    public TeamAPI toTeamAPI() {
        TeamAPI teamAPI = new TeamAPI();
        teamAPI.setId(this.id);
        teamAPI.setNombreEquipo(this.nombreEquipo);
        List<UserAPI> userAPIS = new ArrayList<>();
        for (User user : participantes) {
            userAPIS.add(user.toUserAPISimple());
        }
        teamAPI.setParticipantes(userAPIS);
        teamAPI.setListaDeSubmissions(submissionToSubmissionAPI(this.listaDeSubmissions));
        teamAPI.setListaProblemasCreados(problemToProblemAPI(this.listaProblemasCreados));
        teamAPI.setListaProblemasParticipados(problemToProblemAPI(this.listaProblemasParticipados));
        teamAPI.setListaContestsParticipados(contestToContestAPI(this.listaContestsParticipados));
        teamAPI.setListaContestsCreados(contestToContestAPI(this.listaContestsCreados));
        teamAPI.setTimestamp(this.timestamp);
        return teamAPI;
    }

    public TeamAPI toTeamAPISimple() {
        TeamAPI teamAPI = new TeamAPI();
        teamAPI.setId(this.id);
        teamAPI.setNombreEquipo(this.nombreEquipo);
        return teamAPI;
    }

    private List<ProblemAPI> problemToProblemAPI(Set<Problem> problems) {
        List<ProblemAPI> problemAPIS = new ArrayList<>();
        for (Problem problem : problems) {
            problemAPIS.add(problem.toProblemAPISimple());
        }
        return problemAPIS;
    }

    private List<SubmissionAPI> submissionToSubmissionAPI(Set<Submission> problems) {
        List<SubmissionAPI> listAux = new ArrayList<>();
        for (Submission auxElement : problems) {
            listAux.add(auxElement.toSubmissionAPI());
        }
        return listAux;
    }

    private List<ContestAPI> contestToContestAPI(Set<Contest> problems) {
        List<ContestAPI> listAux = new ArrayList<>();
        for (Contest auxElement : problems) {
            listAux.add(auxElement.toContestAPISimple());
        }
        return listAux;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public Set<User> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(Set<User> participantes) {
        this.participantes = participantes;
    }

    public Set<Submission> getListaDeSubmissions() {
        return listaDeSubmissions;
    }

    public void setListaDeSubmissions(Set<Submission> listaDeSubmissions) {
        this.listaDeSubmissions = listaDeSubmissions;
    }

    public Set<Problem> getListaProblemasParticipados() {
        return listaProblemasParticipados;
    }

    public void setListaProblemasParticipados(Set<Problem> listaProblemasIntentados) {
        this.listaProblemasParticipados = listaProblemasIntentados;
    }

    public Set<Problem> getListaProblemasCreados() {
        return listaProblemasCreados;
    }

    public void setListaProblemasCreados(Set<Problem> listaProblemasCreados) {
        this.listaProblemasCreados = listaProblemasCreados;
    }

    public void addUserToTeam(User user) {
        this.participantes.add(user);
    }

    public void removeUserFromTeam(User user) {
        this.participantes.remove(user);
    }

    public void addProblemaCreado(Problem problem) {
        this.listaProblemasCreados.add(problem);
    }

    public void addProblemaIntentado(Problem problem) {
        this.listaProblemasParticipados.add(problem);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Contest> getListaContestsParticipados() {
        return listaContestsParticipados;
    }

    public void setListaContestsParticipados(Set<Contest> listaContestsParticipados) {
        this.listaContestsParticipados = listaContestsParticipados;
    }

    public Set<Contest> getListaContestsCreados() {
        return listaContestsCreados;
    }

    public void setListaContestsCreados(Set<Contest> listaContestsCreados) {
        this.listaContestsCreados = listaContestsCreados;
    }

    public boolean isEsUser() {
        return esUser;
    }

    public void setEsUser(boolean esUser) {
        this.esUser = esUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id == team.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
