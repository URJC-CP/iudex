package com.example.aplicacion.Entities;
import com.google.common.hash.Hashing;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//En esta clase se mantendra una copia de la ejecucion de un problema por un grupo. Sera la entrada, el codigo, la salidaEstandar, salida error y salida compilador
@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Lob
    private String codigo;
    private String filename;

    @ManyToOne
    private Problem problema;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Result> results;

    private boolean corregido;
    private int numeroResultCorregidos;
    private String resultado;

    @ManyToOne
    private Language language;

    //private Team team;
    private String hashString;
    private String hashStringDelProblema;

    private boolean esPublica;

    public Submission() {
    }

    public Submission(String codigo, Language lenguaje, String filename, boolean esPublica) {
        this.codigo = codigo;
        this.language =lenguaje;
        this.corregido=false;
        this.resultado ="";
        this.results = new ArrayList<>();
        this.filename=filename;
        this.numeroResultCorregidos=0;
        generaHash();
        this.esPublica = esPublica;
    }

    private String listaToString(List<InNOut> lista){
        String salida = new String();
        for (InNOut inout : lista ){
            salida.concat(inout.toString());
        }
        return salida;
    }
    public String generaHash(){
        return  this.hashString = hasheaElString(codigo);
    }

    public String hasheaElString(String string){
        return Hashing.sha256().hashString(string, StandardCharsets.UTF_8).toString();
    }

    @Override
    public String toString() {
        return  codigo + language.getNombreLenguaje();
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
        generaHash();
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
        this.hashStringDelProblema = problema.getHashString();
        this.problema = problema;
    }

    public void addResult(Result res){
        this.results.add(res);
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int isNumeroResultCorregidos() {
        return numeroResultCorregidos;
    }

    public int getNumeroResultCorregidos() {
        return numeroResultCorregidos;
    }

    public void setNumeroResultCorregidos(int numeroResultCorregidos) {
        this.numeroResultCorregidos = numeroResultCorregidos;
    }

    public String getHashString() {
        return hashString;
    }

    public void setHashString(String hashString) {
        this.hashString = hashString;
    }

    public String getHashStringDelProblema() {
        return hashStringDelProblema;
    }

    public void setHashStringDelProblema(String hashStringDelProblema) {
        this.hashStringDelProblema = hashStringDelProblema;
    }

    public void sumarResultCorregido(){
        this.numeroResultCorregidos++;
    }
    public boolean isTerminadoDeEjecutarResults(){
        return this.numeroResultCorregidos == results.size();
    }

    public boolean isEsPublica() {
        return esPublica;
    }

    public void setEsPublica(boolean esPublica) {
        this.esPublica = esPublica;
    }
}