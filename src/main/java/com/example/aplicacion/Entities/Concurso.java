package com.example.aplicacion.Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Concurso {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String nombreConcurso;

    @ManyToOne
    private Team teamPropietario;
    @ManyToMany
    private List<Problem> listaProblemas;
    @ManyToMany
    private List<Team> listaParticipantes;

    public Concurso() {
        this.listaProblemas = new ArrayList<>();
        this.listaParticipantes = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreConcurso() {
        return nombreConcurso;
    }

    public void setNombreConcurso(String nombreConcurso) {
        this.nombreConcurso = nombreConcurso;
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

}
