package com.example.aplicacion.Entities;



import com.example.aplicacion.Pojos.ResultAPI;

import javax.persistence.*;

//Clase que guarda cada uno de los intentos dentro de una submision. Un result por cada entrada y salida esperada.
@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    private String codigo;

    @OneToOne
    private InNOut entradaInO;
    @OneToOne
    private InNOut  salidaEstandarCorrectaInO;

    @Lob
    private String salidaEstandar;
    @Lob
    private String salidaError;
    @Lob
    private String salidaCompilador;


    private int numeroCasoDePrueba;

    private String salidaTime;
    //TIMEOUT FILE fromthe container
    private String signalCompilador;
    private String signalEjecutor;
    private float execTime;
    private float execMemory;

    private boolean revisado;
    @Lob
    private String resultadoRevision;
    @ManyToOne
    private Language language;
    private String fileName;


    private String maxMemory;
    private String maxTimeout;

    public Result() {

        this.maxMemory="100";
        this.maxTimeout="10";
        this.signalCompilador="0";

    }


    public Result(InNOut entrada, String codigo, InNOut salidaEstandarCorrectaInO, Language language, String fileName, String maxtimeout, String maxMemory ) {
        this.codigo=codigo;
        this.entradaInO = entrada;
        this.salidaEstandar="";
        this.salidaError="";
        this.salidaCompilador="";
        this.signalCompilador="0";
        this.signalEjecutor="";
        this.salidaEstandarCorrectaInO = salidaEstandarCorrectaInO;
        this.resultadoRevision="";
        this.language=language;
        this.fileName=fileName;
        this.maxTimeout = maxtimeout;
        this.maxMemory =maxMemory;
    }

    public ResultAPI toResultAPI(){
        ResultAPI resultAPI = new ResultAPI();
        resultAPI.setId(this.id);
        resultAPI.setCodigo(this.codigo);
        resultAPI.setNumeroCasoDePrueba(this.numeroCasoDePrueba);
        resultAPI.setExecTime(this.execTime);
        resultAPI.setExecMemory(this.execMemory);
        resultAPI.setRevisado(this.revisado);
        resultAPI.setResultadoRevision(this.resultadoRevision);
        resultAPI.setLanguage(this.language.toLanguageAPI());

        return resultAPI;
    }
    public ResultAPI toResultAPISimple(){
        ResultAPI resultAPI = new ResultAPI();
        resultAPI.setId(this.id);
        return resultAPI;
    }

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

    @Override
    public String toString() {
        return codigo + entradaInO + salidaCompilador + salidaError + salidaEstandar;
    }

    public String getEntrada() {
        return entradaInO.getText();
    }
    public InNOut getEntradaInO() {
        return entradaInO;
    }


    public void setEntradaInO(InNOut entrada) {
        this.entradaInO = entrada;
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

    public int getNumeroCasoDePrueba() {
        return numeroCasoDePrueba;
    }

    public void setNumeroCasoDePrueba(int numeroEntrada) {
        this.numeroCasoDePrueba = numeroEntrada;
    }

    public String getSalidaEstandarCorrecta() {
        return salidaEstandarCorrectaInO.getText();
    }
    public InNOut getSalidaEstandarCorrectaInO() {
        return salidaEstandarCorrectaInO;
    }

    public void setSalidaEstandarCorrectaInO(InNOut salidaEstandarCorrectaInO) {
        this.salidaEstandarCorrectaInO = salidaEstandarCorrectaInO;
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
}
