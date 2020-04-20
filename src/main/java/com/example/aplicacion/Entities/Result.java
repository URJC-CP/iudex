package com.example.aplicacion.Entities;



import com.example.aplicacion.Repository.InNOutRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

//Clase que guarda cada uno de los intentos dentro de una submision. Un result por cada entrada y salida esperada.
@Entity
public class Result {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    private String codigo;

    @ManyToOne
    private InNOut entradaInO;

    @Lob
    private String salidaEstandar;
    @Lob
    private String salidaError;
    @Lob
    private String salidaCompilador;

    @ManyToOne
    private InNOut  salidaEstandarCorrectaInO;
    private int numeroEntrada;

    private String salidaTime;
    private String timeout;
    private float execTime;
    private float execMemory;

    private boolean revisado;
    private String resultadoRevision;
    @ManyToOne
    private Language language;
    private String fileName;

    public Result() { }


    public Result(InNOut entrada, String codigo, InNOut salidaEstandarCorrectaInO, Language language, String fileName) {
        this.codigo=codigo;
        this.entradaInO = entrada;
        this.salidaEstandar="";
        this.salidaError="";
        this.salidaCompilador="";
        this.salidaEstandarCorrectaInO = salidaEstandarCorrectaInO;
        this.resultadoRevision="";
        this.language=language;
        this.fileName=fileName;
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

    public int getNumeroEntrada() {
        return numeroEntrada;
    }

    public void setNumeroEntrada(int numeroEntrada) {
        this.numeroEntrada = numeroEntrada;
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

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
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

}
