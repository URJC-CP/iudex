package com.example.aplicacion.pojos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class SubmissionAPI {

    private long id;
    private List<ResultAPI> results;
    private TeamAPI team;
    private ProblemAPI problem;

    private Boolean corregido;
    private Integer numeroResultCorregidos;
    private String resultado;

    private LanguageAPI language;

    private Float execSubmissionTime;
    private Float execSubmissionMemory;
    private Long timestamp;


    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProblemAPI getProblem() {
        return problem;
    }

    public void setProblem(ProblemAPI problem) {
        this.problem = problem;
    }

    public List<ResultAPI> getResults() {
        return results;
    }

    public void setResults(List<ResultAPI> results) {
        this.results = results;
    }

    public boolean isCorregido() {
        return corregido;
    }

    public Integer getNumeroResultCorregidos() {
        return numeroResultCorregidos;
    }

    public void setNumeroResultCorregidos(Integer numeroResultCorregidos) {
        this.numeroResultCorregidos = numeroResultCorregidos;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LanguageAPI getLanguage() {
        return language;
    }

    public void setLanguage(LanguageAPI language) {
        this.language = language;
    }

    public Float getExecSubmissionTime() {
        return execSubmissionTime;
    }

    public void setExecSubmissionTime(float execSubmissionTime) {
        this.execSubmissionTime = execSubmissionTime;
    }

    public Float getExecSubmissionMemory() {
        return execSubmissionMemory;
    }

    public void setExecSubmissionMemory(float execSubmissionMemory) {
        this.execSubmissionMemory = execSubmissionMemory;
    }

    public TeamAPI getTeam() {
        return team;
    }

    public void setTeam(TeamAPI team) {
        this.team = team;
    }

    public Boolean getCorregido() {
        return corregido;
    }

    public void setCorregido(boolean corregido) {
        this.corregido = corregido;
    }

    public void setCorregido(Boolean corregido) {
        this.corregido = corregido;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
