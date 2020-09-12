package com.example.aplicacion.Entities;

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
    private List<Concurso> listaConcursosParticipados;
    @OneToMany
    private List<Concurso> listaConcursosCreados;

    public Team(){
        this.participantes = new ArrayList<>();
        this.listaDeSubmissions = new ArrayList<>();
        this.listaProblemasCreados = new ArrayList<>();
        this.listaProblemasParticipados = new ArrayList<>();
        this.listaConcursosCreados= new ArrayList<>();
        this.listaConcursosParticipados= new ArrayList<>();

    }
    public Team(String nombreEquipo){
        this.nombreEquipo=nombreEquipo;
        this.participantes = new ArrayList<>();
        this.listaDeSubmissions = new ArrayList<>();
        this.listaProblemasCreados = new ArrayList<>();
        this.listaProblemasParticipados = new ArrayList<>();
        this.listaConcursosCreados= new ArrayList<>();
        this.listaConcursosParticipados= new ArrayList<>();
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

    public List<Concurso> getListaConcursosParticipados() {
        return listaConcursosParticipados;
    }

    public void setListaConcursosParticipados(List<Concurso> listaConcursosParticipados) {
        this.listaConcursosParticipados = listaConcursosParticipados;
    }

    public List<Concurso> getListaConcursosCreados() {
        return listaConcursosCreados;
    }

    public void setListaConcursosCreados(List<Concurso> listaConcursosCreados) {
        this.listaConcursosCreados = listaConcursosCreados;
    }
}
