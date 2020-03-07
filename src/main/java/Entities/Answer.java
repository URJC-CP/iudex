package Entities;
import javax.persistence.*;


import java.io.File;

//En esta clase se mantendra una copia de la ejecucion de un ejercicio por un grupo. Sera la entrada, el codigo, la salidaEstandar, salida error y salida compilador
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private File entrada;
    private File codigo;
    private String salidaEstandar;
    private String salidaError;
    private String salidaCompilador;

    public Answer() {
    }
    public Answer(File entrada, File codigo) {
        this.entrada=entrada;
        this.codigo=codigo;
    }

    public File getEntrada() {
        return entrada;
    }

    public void setEntrada(File entrada) {
        this.entrada = entrada;
    }

    public File getCodigo() {
        return codigo;
    }

    public void setCodigo(File codigo) {
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

    public String getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    private String lenguaje;
    private Team team;



}
