package com.example.aplicacion.Pojos;

public class ResultAPI {
    private long id;

    //Hay q tener en cuenta que otros usuarios no deberian ver el codigo, pero lo dejo dispobible
    private String codigo;

    private String salidaTime;

    private int numeroCasoDePrueba;

    private float execTime;
    private float execMemory;
    private boolean revisado;
    private String resultadoRevision;

    private LanguageAPI language;





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

    public String getSalidaTime() {
        return salidaTime;
    }

    public void setSalidaTime(String salidaTime) {
        this.salidaTime = salidaTime;
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

    public LanguageAPI getLanguage() {
        return language;
    }

    public void setLanguage(LanguageAPI language) {
        this.language = language;
    }



    public int getNumeroCasoDePrueba() {
        return numeroCasoDePrueba;
    }

    public void setNumeroCasoDePrueba(int numeroCasoDePrueba) {
        this.numeroCasoDePrueba = numeroCasoDePrueba;
    }
}
