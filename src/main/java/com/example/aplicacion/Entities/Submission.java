package com.example.aplicacion.Entities;
import com.example.aplicacion.Pojos.ResultAPI;
import com.example.aplicacion.Pojos.SubmissionAPI;
import com.google.common.hash.Hashing;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
    @ManyToOne
    private Contest contest;
    @ManyToOne
    private Team team;

    private String hashStringSubmission;
    private String hashStringDelProblema;
    private boolean esProblemValidator;
    private String esProblemValidatorResultadoEsperado;

    private float execSubmissionTime;
    private float execSubmissionMemory;
    private long timestamp=  Instant.now().toEpochMilli();



    //Cuando vaya a borrar busca que no tenga una relaccion con problemvalidator, si lo es se desvincula del contest
    @PreRemove
    private void removeContestFromProblemValidator(){

    }


    public Submission() {
        numeroResultCorregidos =0;
    }

    public Submission(String codigo, Language lenguaje, String filename) {
        this.codigo = codigo;
        this.language =lenguaje;
        this.corregido=false;
        this.resultado ="";
        this.results = new ArrayList<>();
        this.filename=filename;
        this.numeroResultCorregidos=0;

        generaHash();
    }
    public SubmissionAPI toSubmissionAPI(){
        SubmissionAPI submissionAPI = new SubmissionAPI();
        submissionAPI.setId(this.id);
        List<ResultAPI> resultAPIS = new ArrayList<>();
        for(Result result: this.results){
            resultAPIS.add(result.toResultAPISimple());
        }
        submissionAPI.setTeam(this.team.toTeamAPISimple());
        submissionAPI.setResults(resultAPIS);
        submissionAPI.setCorregido(this.corregido);
        submissionAPI.setNumeroResultCorregidos(this.numeroResultCorregidos);
        submissionAPI.setResultado(this.resultado);
        submissionAPI.setLanguage(this.language.toLanguageAPI());
        submissionAPI.setExecSubmissionTime(this.execSubmissionTime);
        submissionAPI.setExecSubmissionMemory(this.execSubmissionMemory);
        submissionAPI.setTimestamp(this.timestamp);
        return submissionAPI;
    }
    public SubmissionAPI toSubmissionAPISimple() {
        SubmissionAPI submissionAPI = new SubmissionAPI();
        submissionAPI.setId(this.id);
        submissionAPI.setTeam(this.team.toTeamAPISimple());
        return submissionAPI;
    }
    private String listaToString(List<InNOut> lista){
        String salida = new String();
        for (InNOut inout : lista ){
            salida.concat(inout.toString());
        }
        return salida;
    }
    public String generaHash(){
        return  this.hashStringSubmission = hasheaElString(codigo);
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
    public void generaHashProblema(){
        this.hashStringDelProblema = problema.getHashString();

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

    public String getHashStringSubmission() {
        return hashStringSubmission;
    }

    public void setHashStringSubmission(String hashString) {
        this.hashStringSubmission = hashString;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}