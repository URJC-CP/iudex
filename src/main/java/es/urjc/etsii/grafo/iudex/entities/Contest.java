package es.urjc.etsii.grafo.iudex.entities;

import es.urjc.etsii.grafo.iudex.pojos.ContestAPI;
import es.urjc.etsii.grafo.iudex.pojos.LanguageAPI;
import es.urjc.etsii.grafo.iudex.pojos.ProblemAPI;
import es.urjc.etsii.grafo.iudex.pojos.TeamAPI;

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

    @OneToMany(mappedBy = "contest")
    Set<ContestProblem> listaProblemas;

    @OneToMany(mappedBy= "lenguajes")
    private Set<ContestLanguages> lenguajes;

    @OneToMany(mappedBy = "listaContestsParticipados")
    private Set<ContestTeams> listaContestsParticipados;

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL)
    private Set<Submission> listaSubmissions;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public Contest() {
        this.listaProblemas = new HashSet<>();
        this.listaContestsParticipados = new HashSet<>();
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
        for (ContestLanguages lenguaje : lenguajes) {
            lenguajesAceptados.add(lenguaje.getLenguajes().toLanguageAPISimple());
        }
        contestAPI.setLenguajesAceptados(lenguajesAceptados);

        List<ProblemAPI> listaProblemas = new ArrayList<>();
        for (ContestProblem problem : this.listaProblemas) {
            listaProblemas.add(problem.getProblem().toProblemAPISimple());
        }
        contestAPI.setListaProblemas(listaProblemas);

        List<TeamAPI> teamAPIS = new ArrayList<>();
        for (ContestTeams team : this.listaContestsParticipados) {
            teamAPIS.add(team.getTeams().toTeamAPISimple());
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
        for (ContestLanguages lenguaje : lenguajes) {
            lenguajesAceptados.add(lenguaje.getLenguajes().toLanguageAPISimple());
        }
        contestAPI.setLenguajesAceptados(lenguajesAceptados);

        List<ProblemAPI> problemAPIS = new ArrayList<>();
        for (ContestProblem problem : this.listaProblemas) {
            problemAPIS.add(problem.getProblem().toProblemAPI());
        }
        contestAPI.setListaProblemas(problemAPIS);

        List<TeamAPI> teamAPIS = new ArrayList<>();
        for (ContestTeams team : this.listaContestsParticipados) {
            teamAPIS.add(team.getTeams().toTeamAPISimple());
        }
        contestAPI.setListaParticipantes(teamAPIS);

        contestAPI.setStartDateTime(convertLocalDateTimeToMillis(this.startDateTime));
        contestAPI.setEndDateTime(convertLocalDateTimeToMillis(this.endDateTime));
        return contestAPI;
    }

    public Contest(Set<ContestProblem> listaProblemas) {
        this.listaProblemas = listaProblemas;
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

    public Set<ContestProblem> getListaProblemas() {
        return listaProblemas;
    }

    public void setListaProblemas(Set<ContestProblem> listaProblemas) {
        this.listaProblemas = listaProblemas;
    }

    public Set<ContestLanguages> getLenguajes() {
        return lenguajes;
    }

    public void setLenguajes(Set<ContestLanguages> lenguajes) {
        this.lenguajes = lenguajes;
    }

    public void addLanguage(ContestLanguages language) {
        lenguajes.add(language);
    }

    public void clearLanguage() {
        lenguajes.clear();
    }

    public void removeLanguage(ContestLanguages language) {
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


    public Set<ContestTeams> getListaContestsParticipados() {
        return listaContestsParticipados;
    }

    public void setListaContestsParticipados(Set<ContestTeams> listaContestsParticipados) {
        this.listaContestsParticipados = listaContestsParticipados;
    }

    public void addProblem(ContestProblem problem) {
        this.listaProblemas.add(problem);
    }

    public void deleteProblem(ContestProblem problem) {
        this.listaProblemas.remove(problem);
    }

    public void addTeam(ContestTeams team) {
        this.listaContestsParticipados.add(team);
    }

    public void deleteTeam(ContestTeams team) {
        this.listaContestsParticipados.remove(team);
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
