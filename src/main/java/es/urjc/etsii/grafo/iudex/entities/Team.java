package es.urjc.etsii.grafo.iudex.entities;

import es.urjc.etsii.grafo.iudex.pojos.*;

import jakarta.persistence.*;
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    private Set<TeamUser> participantes;
    private boolean esUser;
    
    @OneToMany(mappedBy = "contest")
    Set<ContestProblem> listaProblemas;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<Submission> listaDeSubmissions;

    @OneToMany(mappedBy = "teams")
    private Set<TeamsProblems> listaProblemsCreados;

    @OneToMany(mappedBy = "teams")
    private Set<TeamsProblems> listaProblemsParticipados;

    @OneToMany(mappedBy = "teams")
    private Set<ContestTeams> listaContestsParticipados;

    @OneToMany(mappedBy = "teams")
    private Set<ContestTeams> listaContestsCreados;

    private long timestamp = Instant.now().toEpochMilli();

    public Team() {
        
    }

    public Team(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
        this.participantes = new HashSet<>();
        this.listaDeSubmissions = new HashSet<>();
        this.listaProblemsCreados = new HashSet<>();
        this.listaProblemsParticipados = new HashSet<>();
        this.listaContestsCreados = new HashSet<>();
        this.listaContestsParticipados = new HashSet<>();
    }

    public TeamAPI toTeamAPI() {
        TeamAPI teamAPI = new TeamAPI();
        teamAPI.setId(this.id);
        teamAPI.setNombreEquipo(this.nombreEquipo);
        List<UserAPI> userAPIS = new ArrayList<>();
        for (TeamUser user : participantes) {
            userAPIS.add(user.getUser().toUserAPISimple());
        }
        teamAPI.setParticipantes(userAPIS);
        teamAPI.setListaDeSubmissions(submissionToSubmissionAPI(this.listaDeSubmissions));
        teamAPI.setListaProblemasCreados(problemToProblemAPI(this.listaProblemsCreados));
        teamAPI.setListaProblemasParticipados(problemToProblemAPI(this.listaProblemsParticipados));
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

    private List<ProblemAPI> problemToProblemAPI(Set<TeamsProblems> problems) {
        List<ProblemAPI> problemAPIS = new ArrayList<>();
        for (TeamsProblems problem : problems) {
            problemAPIS.add(problem.getProblem().toProblemAPISimple());
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

    private List<ContestAPI> contestToContestAPI(Set<ContestTeams> concursos) {
        List<ContestAPI> listAux = new ArrayList<>();
        for (ContestTeams auxElement : concursos) {
            listAux.add(auxElement.getContest().toContestAPISimple());
        }
        return listAux;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public Set<TeamUser> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(Set<TeamUser> participantes) {
        this.participantes = participantes;
    }

    public Set<Submission> getListaDeSubmissions() {
        return listaDeSubmissions;
    }

    public void setListaDeSubmissions(Set<Submission> listaDeSubmissions) {
        this.listaDeSubmissions = listaDeSubmissions;
    }

    public Set<TeamsProblems> getListaProblemasCreados() {
        return listaProblemsCreados;
    }

    public void setListaProblemasCreados(Set<TeamsProblems> listaProblemasCreados) {
        this.listaProblemsCreados = listaProblemasCreados;
    }

    public Set<TeamsProblems> getListaProblemasParticipados() {
        return listaProblemsCreados;
    }

    public void setListaProblemasParticipados(Set<TeamsProblems> listaProblemsParticipados) {
        this.listaProblemsParticipados = listaProblemsParticipados;
    }

    public void addUserToTeam(TeamUser user) {
        this.participantes.add(user);
    }

    public void removeUserFromTeam(TeamUser user) {
        this.participantes.remove(user);
    }

    public void addProblemCreado(TeamsProblems problem) {
        this.listaProblemsCreados.add(problem);
    }

    public void addProblemIntentado(TeamsProblems problem) {
        this.listaProblemsParticipados.add(problem);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Team(Set<ContestTeams> listaContestsParticipados) {
        this.listaContestsParticipados = listaContestsParticipados;
    }
    
    public void setListaContestsParticipados(Set<ContestTeams> listaContestsParticipados) {
        this.listaContestsParticipados = listaContestsParticipados;
    }


    public Set<ContestTeams> getListaContestsParticipados() {
        return listaContestsParticipados;
    }

    public Set<ContestTeams> getListaContestsCreados() {
        return listaContestsCreados;
    }

    public void setListaContestsCreados(Set<ContestTeams> listaContestsCreados) {
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
