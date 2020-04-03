package com.example.aplicacion.Entities;



import javax.persistence.*;

//Clase que guarda cada uno de los intentos dentro de una submision. Un result por cada entrada y salida esperada.
@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    private String codigo;
    @Lob
    private String entrada;

    @Lob
    private String salidaEstandar;
    @Lob
    private String salidaError;
    @Lob
    private String salidaCompilador;
    @Lob
    private String  salidaEstandarCorrecta;
    private int numeroEntrada;

    private boolean revisado;
    private String resultadoRevision;

    public Result() { }


    public Result(String entrada, String codigo, String salidaEstandarCorrecta) {
        this.codigo=codigo;
        this.entrada = entrada;
        this.salidaEstandar="";
        this.salidaError="";
        this.salidaCompilador="";
        this.salidaEstandarCorrecta = salidaEstandarCorrecta;

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
        return codigo + entrada + salidaCompilador + salidaError + salidaEstandar;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
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
        return salidaEstandarCorrecta;
    }

    public void setSalidaEstandarCorrecta(String salidaEstandarCorrecta) {
        this.salidaEstandarCorrecta = salidaEstandarCorrecta;
    }
}
