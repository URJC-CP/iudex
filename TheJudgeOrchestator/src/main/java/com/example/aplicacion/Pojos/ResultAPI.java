package com.example.aplicacion.Pojos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResultAPI {
    private Long id;

    //Hay q tener en cuenta que otros usuarios no deberian ver el codigo, pero lo dejo dispobible
    private String codigo;

    private String salidaTime;

    private Integer numeroCasoDePrueba;

    private Float execTime;
    private Float execMemory;
    private Boolean revisado;
    private String resultadoRevision;

    private LanguageAPI language;
    private long timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSalidaTime() {
        return salidaTime;
    }

    public void setSalidaTime(String salidaTime) {
        this.salidaTime = salidaTime;
    }

    public Integer getNumeroCasoDePrueba() {
        return numeroCasoDePrueba;
    }

    public void setNumeroCasoDePrueba(Integer numeroCasoDePrueba) {
        this.numeroCasoDePrueba = numeroCasoDePrueba;
    }

    public Float getExecTime() {
        return execTime;
    }

    public void setExecTime(Float execTime) {
        this.execTime = execTime;
    }

    public Float getExecMemory() {
        return execMemory;
    }

    public void setExecMemory(Float execMemory) {
        this.execMemory = execMemory;
    }

    public Boolean getRevisado() {
        return revisado;
    }

    public void setRevisado(Boolean revisado) {
        this.revisado = revisado;
    }

    public String getResultadoRevision() {
        return resultadoRevision;
    }

    public void setResultadoRevision(String resultadoRevision) {
        this.resultadoRevision = resultadoRevision;
    }

    public LanguageAPI getLanguage() {
        return language;
    }

    public void setLanguage(LanguageAPI language) {
        this.language = language;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
