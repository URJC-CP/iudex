package com.example.aplicacion.Entities;
import javax.persistence.*;

//En esta clase se mantendra una copia de la ejecucion de un ejercicio por un grupo. Sera la entrada, el codigo, la salidaEstandar, salida error y salida compilador
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    private String entrada;
    @Lob
    private String codigo;
    private String salidaEstandar;
    private String salidaError;
    private String salidaCompilador;

    @ManyToOne
    private Exercise ejercicio;
    private String lenguaje;
    private boolean corregido;
    private String resultado;

    //private Team team;

    public String getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
    }



    public Answer() {
    }

    public Answer(String codigo, String entrada, String lenguaje) {
        this.entrada = entrada;
        this.codigo = codigo;
        this.lenguaje =lenguaje;
        this.corregido=false;
        this.resultado ="";
    }

    @Override
    public String toString() {
        return entrada + codigo + lenguaje;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
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

    public boolean isCorregido() {
        return corregido;
    }

    public void setCorregido(boolean correjido) {
        this.corregido = correjido;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Exercise getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Exercise ejercicio) {
        this.ejercicio = ejercicio;
    }
}