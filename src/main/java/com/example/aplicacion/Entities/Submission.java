package com.example.aplicacion.Entities;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//En esta clase se mantendra una copia de la ejecucion de un problema por un grupo. Sera la entrada, el codigo, la salidaEstandar, salida error y salida compilador
@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    @Lob
    private String codigo;

    @ManyToOne
    private Problem problema;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Result> results;

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



    public Submission() {
    }

    public Submission(String codigo, String lenguaje) {
        this.codigo = codigo;
        this.lenguaje =lenguaje;
        this.corregido=false;
        this.resultado ="";
        this.results = new ArrayList<>();
    }

    @Override
    public String toString() {
        return  codigo + lenguaje;
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

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
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

    public Problem getProblema() {
        return problema;
    }

    public void setProblema(Problem problema) {
        this.problema = problema;
    }

    public void addResult(Result res){
        this.results.add(res);
    }
}