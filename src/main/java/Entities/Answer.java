package Entities;
import javax.persistence.*;


import java.io.File;

//En esta clase se mantendra una copia de la ejecucion de un ejercicio por un grupo. Sera la entrada, el codigo, la salidaEstandar, salida error y salida compilador
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private String entrada;
    private String codigo;
    private String salidaEstandar;
    private String salidaError;
    private String salidaCompilador;

    public Answer() {
    }

    public Answer(String codigo, String entrada) {
        this.entrada = entrada;
        this.codigo = codigo;
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
}