package com.example.aplicacion.Entities;

import com.example.aplicacion.Pojos.TeamAPI;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//CLASE donde se guarda la informacion referente a los equipos
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String nombreEquipo;

    @ManyToMany (fetch = FetchType.EAGER, mappedBy = "equiposParticipantes")
    private List<User> participantes;

    @OneToMany
    private List<Submission> listaDeSubmissions;
    @OneToMany
    private List<Problem> listaProblemasCreados;
    @ManyToMany
    private List<Problem> listaProblemasParticipados;
    @ManyToMany(mappedBy = "listaParticipantes")
    private List<Contest> listaContestsParticipados;
    @OneToMany
    private List<Contest> listaContestsCreados;

    public Team(){
        this.participantes = new ArrayList<>();
        this.listaDeSubmissions = new ArrayList<>();
        this.listaProblemasCreados = new ArrayList<>();
        this.listaProblemasParticipados = new ArrayList<>();
        this.listaContestsCreados= new ArrayList<>();
        this.listaContestsParticipados= new ArrayList<>();

    }
    public Team(String nombreEquipo){
        this.nombreEquipo=nombreEquipo;
        this.participantes = new ArrayList<>();
        this.listaDeSubmissions = new ArrayList<>();
        this.listaProblemasCreados = new ArrayList<>();
        this.listaProblemasParticipados = new ArrayList<>();
        this.listaContestsCreados= new ArrayList<>();
        this.listaContestsParticipados= new ArrayList<>();
    }
    public TeamAPI toTeamAPI(){
        TeamAPI teamAPI = new TeamAPI();
        teamAPI.setId(this.id);
        teamAPI.setNombreEquipo(this.nombreEquipo);

        return teamAPI;
    }

    public TeamAPI toTeamAPISimple(){
        TeamAPI teamAPI = new TeamAPI();
        teamAPI.setId(this.id);
        teamAPI.setNombreEquipo(this.nombreEquipo);
        return teamAPI;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public List<User> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<User> participantes) {
        this.participantes = participantes;
    }

    public List<Submission> getListaDeSubmissions() {
        return listaDeSubmissions;
    }

    public void setListaDeSubmissions(List<Submission> listaDeSubmissions) {
        this.listaDeSubmissions = listaDeSubmissions;
    }

    public List<Problem> getListaProblemasParticipados() {
        return listaProblemasParticipados;
    }

    public void setListaProblemasParticipados(List<Problem> listaProblemasIntentados) {
        this.listaProblemasParticipados = listaProblemasIntentados;
    }

    public List<Problem> getListaProblemasCreados() {
        return listaProblemasCreados;
    }

    public void setListaProblemasCreados(List<Problem> listaProblemasCreados) {
        this.listaProblemasCreados = listaProblemasCreados;
    }

    public void addUserToTeam(User user){
        this.participantes.add(user);
    }
    public void removeUserFromTeam(User user){
        this.participantes.remove(user);
    }

    public void addProblemaCreado(Problem problem){
        this.listaProblemasCreados.add(problem);
    }
    public void addProblemaIntentado(Problem problem){
        this.listaProblemasParticipados.add(problem);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Contest> getListaContestsParticipados() {
        return listaContestsParticipados;
    }

    public void setListaContestsParticipados(List<Contest> listaContestsParticipados) {
        this.listaContestsParticipados = listaContestsParticipados;
    }

    public List<Contest> getListaContestsCreados() {
        return listaContestsCreados;
    }

    public void setListaContestsCreados(List<Contest> listaContestsCreados) {
        this.listaContestsCreados = listaContestsCreados;
    }
}
