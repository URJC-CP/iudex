package com.example.aplicacion.Pojos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContestAPI {
    private long id;

    private String nombreContest;
    private String descripcion;
    private List<LanguageAPI> lenguajesAceptados;
    private TeamAPI teamPropietario;
    private List<ProblemAPI> listaProblemas;
    private List<TeamAPI> listaParticipantes;
    private long timestamp;

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

    public TeamAPI getTeamPropietario() {
        return teamPropietario;
    }

    public void setTeamPropietario(TeamAPI teamPropietario) {
        this.teamPropietario = teamPropietario;
    }

    public List<LanguageAPI> getLenguajesAceptados() {
        return lenguajesAceptados;
    }

    public void setLenguajesAceptados(List<LanguageAPI> lenguagesAceptados) {
        this.lenguajesAceptados = lenguagesAceptados;
    }

    public List<ProblemAPI> getListaProblemas() {
        return listaProblemas;
    }

    public void setListaProblemas(List<ProblemAPI> listaProblemas) {
        this.listaProblemas = listaProblemas;
    }

    public List<TeamAPI> getListaParticipantes() {
        return listaParticipantes;
    }

    public void setListaParticipantes(List<TeamAPI> listaParticipantes) {
        this.listaParticipantes = listaParticipantes;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
