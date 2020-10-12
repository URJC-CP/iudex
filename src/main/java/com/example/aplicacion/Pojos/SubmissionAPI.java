package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.LanguageAPI;

import java.util.List;

public class SubmissionAPI {

    private long id;
    private String codigo;
    private String filename;
    private List<ResultAPI> results;

    private boolean corregido;
    private int numeroResultCorregidos;
    private String resultado;

    private LanguageAPI language;

    private String hashStringSubmission;
    private String hashStringDelProblema;
    private boolean esProblemValidator;
    private String esProblemValidatorResultadoEsperado;

    private float execSubmissionTime;
    private float execSubmissionMemory;


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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public void setCorregido(boolean corregido) {
        this.corregido = corregido;
    }

    public int getNumeroResultCorregidos() {
        return numeroResultCorregidos;
    }

    public void setNumeroResultCorregidos(int numeroResultCorregidos) {
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

    public String getHashStringSubmission() {
        return hashStringSubmission;
    }

    public void setHashStringSubmission(String hashStringSubmission) {
        this.hashStringSubmission = hashStringSubmission;
    }

    public String getHashStringDelProblema() {
        return hashStringDelProblema;
    }

    public void setHashStringDelProblema(String hashStringDelProblema) {
        this.hashStringDelProblema = hashStringDelProblema;
    }

    public boolean isEsProblemValidator() {
        return esProblemValidator;
    }

    public void setEsProblemValidator(boolean esProblemValidator) {
        this.esProblemValidator = esProblemValidator;
    }

    public String getEsProblemValidatorResultadoEsperado() {
        return esProblemValidatorResultadoEsperado;
    }

    public void setEsProblemValidatorResultadoEsperado(String esProblemValidatorResultadoEsperado) {
        this.esProblemValidatorResultadoEsperado = esProblemValidatorResultadoEsperado;
    }

    public float getExecSubmissionTime() {
        return execSubmissionTime;
    }

    public void setExecSubmissionTime(float execSubmissionTime) {
        this.execSubmissionTime = execSubmissionTime;
    }

    public float getExecSubmissionMemory() {
        return execSubmissionMemory;
    }

    public void setExecSubmissionMemory(float execSubmissionMemory) {
        this.execSubmissionMemory = execSubmissionMemory;
    }
}
