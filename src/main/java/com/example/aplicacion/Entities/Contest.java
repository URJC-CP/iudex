package com.example.aplicacion.Entities;

import com.example.aplicacion.Pojos.ContestAPI;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Pojos.TeamAPI;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany
    private List<Team> listaParticipantes;
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL)
    private List<Submission> listaSubmissions;


    public Contest() {
        this.listaProblemas = new ArrayList<>();
        this.listaParticipantes = new ArrayList<>();
        this.listaSubmissions = new ArrayList<>();
        this.listaProblemas = new ArrayList<>();
    }

    public ContestAPI toContestAPI()  {
        ContestAPI contestAPI = new ContestAPI();
        contestAPI.setId(this.id);
        contestAPI.setNombreContest(this.nombreContest);
        contestAPI.setDescripcion(this.descripcion);
        contestAPI.setTeamPropietario(this.teamPropietario.toTeamAPISimple());
        List<ProblemAPI> listaProblemass = new ArrayList<>();
        for(Problem problem: this.listaProblemas){
            listaProblemass.add(problem.toProblemAPISimple());
        }
        contestAPI.setListaProblemas(listaProblemass);

        List<TeamAPI> teamAPIS = new ArrayList<>();
        for(Team team: this.listaParticipantes){
            teamAPIS.add(team.toTeamAPISimple());
        }
        return contestAPI;
    }
    public ContestAPI toContestAPIFull()  {
        ContestAPI contestAPI = new ContestAPI();
        contestAPI.setId(this.id);
        contestAPI.setNombreContest(this.nombreContest);
        contestAPI.setDescripcion(this.descripcion);
        contestAPI.setTeamPropietario(this.teamPropietario.toTeamAPISimple());
        List<ProblemAPI> listaProblemass = new ArrayList<>();
        for(Problem problem: this.listaProblemas){
            listaProblemass.add(problem.toProblemAPI());
        }
        contestAPI.setListaProblemas(listaProblemass);

        List<TeamAPI> teamAPIS = new ArrayList<>();
        for(Team team: this.listaParticipantes){
            teamAPIS.add(team.toTeamAPISimple());
        }
        return contestAPI;
    }
    public ContestAPI toContestAPISimple(){
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

    public List<Team> getListaParticipantes() {
        return listaParticipantes;
    }

    public void setListaParticipantes(List<Team> listaParticipantes) {
        this.listaParticipantes = listaParticipantes;
    }

    public void addProblem(Problem problem){
        this.listaProblemas.add(problem);
    }
    public void deleteProblem(Problem problem){this.listaProblemas.remove(problem );}

    public void addTeam(Team team){
        this.listaParticipantes.add(team);
    }

    public void deleteTeam(Team team){
        this.listaParticipantes.remove(teamPropietario);
    }

    public List<Submission> getListaSubmissions() {
        return listaSubmissions;
    }

    public void setListaSubmissions(List<Submission> listaSubmissions) {
        this.listaSubmissions = listaSubmissions;
    }

    public void addSubmission(Submission submission){
        this.listaSubmissions.add(submission);
    }
    public void removeSubmission(Submission submission){
        this.listaSubmissions.remove(submission);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String description) {
        this.descripcion = description;
    }
}
