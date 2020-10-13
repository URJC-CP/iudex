package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Concurso;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.User;

import java.util.List;

public class TeamAPI {
    private long id;
    private String nombreEquipo;

    private List<UserAPI> participantes;
    private List<SubmissionAPI> listaDeSubmissions;
    private List<ProblemAPI> listaProblemasCreados;
    private List<ProblemAPI> listaProblemasParticipados;
    private List<ConcursoAPI> listaConcursosParticipados;
    private List<ConcursoAPI> listaConcursosCreados;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public List<UserAPI> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<UserAPI> participantes) {
        this.participantes = participantes;
    }

    public List<SubmissionAPI> getListaDeSubmissions() {
        return listaDeSubmissions;
    }

    public void setListaDeSubmissions(List<SubmissionAPI> listaDeSubmissions) {
        this.listaDeSubmissions = listaDeSubmissions;
    }

    public List<ProblemAPI> getListaProblemasCreados() {
        return listaProblemasCreados;
    }

    public void setListaProblemasCreados(List<ProblemAPI> listaProblemasCreados) {
        this.listaProblemasCreados = listaProblemasCreados;
    }

    public List<ProblemAPI> getListaProblemasParticipados() {
        return listaProblemasParticipados;
    }

    public void setListaProblemasParticipados(List<ProblemAPI> listaProblemasParticipados) {
        this.listaProblemasParticipados = listaProblemasParticipados;
    }

    public List<ConcursoAPI> getListaConcursosParticipados() {
        return listaConcursosParticipados;
    }

    public void setListaConcursosParticipados(List<ConcursoAPI> listaConcursosParticipados) {
        this.listaConcursosParticipados = listaConcursosParticipados;
    }

    public List<ConcursoAPI> getListaConcursosCreados() {
        return listaConcursosCreados;
    }

    public void setListaConcursosCreados(List<ConcursoAPI> listaConcursosCreados) {
        this.listaConcursosCreados = listaConcursosCreados;
    }
}
