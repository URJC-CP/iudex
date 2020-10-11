package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Language;

public class ResultAPI {
    private long id;

    private String codigo;
    private String salidaEstandar;
    private String salidaError;
    private String salidaCompilador;

    private String salidaTime;
    //TIMEOUT FILE fromthe container
    private String signalCompilador;
    private String signalEjecutor;
    private float execTime;
    private float execMemory;
    private boolean revisado;
    private String resultadoRevision;

    private Language language;
    private String fileName;


    private String maxMemory;
    private String maxTimeout;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSalidaEstandar() {
        return salidaEstandar;
    }

    public void setSalidaEstandar(String salidaEstandar) {
        this.salidaEstandar = salidaEstandar;
    }

    public String getSalidaError() {
        return salidaError;
    }

    public void setSalidaError(String salidaError) {
        this.salidaError = salidaError;
    }

    public String getSalidaCompilador() {
        return salidaCompilador;
    }

    public void setSalidaCompilador(String salidaCompilador) {
        this.salidaCompilador = salidaCompilador;
    }

    public String getSalidaTime() {
        return salidaTime;
    }

    public void setSalidaTime(String salidaTime) {
        this.salidaTime = salidaTime;
    }

    public String getSignalCompilador() {
        return signalCompilador;
    }

    public void setSignalCompilador(String signalCompilador) {
        this.signalCompilador = signalCompilador;
    }

    public String getSignalEjecutor() {
        return signalEjecutor;
    }

    public void setSignalEjecutor(String signalEjecutor) {
        this.signalEjecutor = signalEjecutor;
    }

    public float getExecTime() {
        return execTime;
    }

    public void setExecTime(float execTime) {
        this.execTime = execTime;
    }

    public float getExecMemory() {
        return execMemory;
    }

    public void setExecMemory(float execMemory) {
        this.execMemory = execMemory;
    }

    public boolean isRevisado() {
        return revisado;
    }

    public void setRevisado(boolean revisado) {
        this.revisado = revisado;
    }

    public String getResultadoRevision() {
        return resultadoRevision;
    }

    public void setResultadoRevision(String resultadoRevision) {
        this.resultadoRevision = resultadoRevision;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(String maxMemory) {
        this.maxMemory = maxMemory;
    }

    public String getMaxTimeout() {
        return maxTimeout;
    }

    public void setMaxTimeout(String maxTimeout) {
        this.maxTimeout = maxTimeout;
    }
}
