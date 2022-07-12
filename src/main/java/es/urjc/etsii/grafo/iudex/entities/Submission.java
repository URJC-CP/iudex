package es.urjc.etsii.grafo.iudex.entities;

import es.urjc.etsii.grafo.iudex.pojos.ResultAPI;
import es.urjc.etsii.grafo.iudex.pojos.SubmissionAPI;
import com.google.common.hash.Hashing;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

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
    private Problem problem;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Result> results;

    private boolean corregido;
    private int numeroResultCorregidos;
    @Lob
    private String result;

    @ManyToOne
    private Language language;
    @ManyToOne
    private Contest contest;
    @ManyToOne
    private Team team;

    private String hashStringSubmission;

    private String hashStringDelProblem;
    private boolean esProblemValidator;
    private String esProblemValidatorResultEsperado;

    private float execSubmissionTime;
    private float execSubmissionMemory;
    private long timestamp = Instant.now().toEpochMilli();

    public Submission() {
        numeroResultCorregidos = 0;
    }

    public Submission(String codigo, Language lenguaje, String filename) {
        this.codigo = codigo;
        this.language = lenguaje;
        this.corregido = false;
        this.result = "";
        this.results = new HashSet<>();
        this.filename = filename;
        this.numeroResultCorregidos = 0;

        generaHash();
    }

    @PreRemove
    private void removeContestFromProblemValidator() {
        //Cuando vaya a borrar busca que no tenga una relaccion con problemvalidator, si lo es se desvincula del contest
    }

    public SubmissionAPI toSubmissionAPI() {
        SubmissionAPI submissionAPI = new SubmissionAPI();
        submissionAPI.setId(this.id);
        List<ResultAPI> resultAPIS = new ArrayList<>();
        for (Result result : this.results) {
            resultAPIS.add(result.toResultAPISimple());
        }
        submissionAPI.setProblem(this.getProblem().toProblemAPISimple());
        submissionAPI.setTeam(this.team.toTeamAPISimple());
        submissionAPI.setResults(resultAPIS);
        submissionAPI.setCorregido(this.corregido);
        submissionAPI.setNumeroResultCorregidos(this.numeroResultCorregidos);
        submissionAPI.setResultado(this.result);
        submissionAPI.setLanguage(this.language.toLanguageAPI());
        submissionAPI.setExecSubmissionTime(this.execSubmissionTime);
        submissionAPI.setExecSubmissionMemory(this.execSubmissionMemory);
        submissionAPI.setTimestamp(this.timestamp);
        return submissionAPI;
    }

    public SubmissionAPI toSubmissionAPIFull() {
        SubmissionAPI submissionAPI = new SubmissionAPI();
        submissionAPI.setId(this.id);
        List<ResultAPI> resultAPIS = new ArrayList<>();
        for (Result result : this.results) {
            resultAPIS.add(result.toResultAPI());
        }
        submissionAPI.setProblem(this.getProblem().toProblemAPISimple());
        submissionAPI.setTeam(this.team.toTeamAPISimple());
        submissionAPI.setResults(resultAPIS);
        submissionAPI.setCorregido(this.corregido);
        submissionAPI.setNumeroResultCorregidos(this.numeroResultCorregidos);
        submissionAPI.setResultado(this.result);
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
        submissionAPI.setResultado(this.result);
        submissionAPI.setTimestamp(this.timestamp);
        return submissionAPI;
    }

    public String generaHash() {
        this.hashStringSubmission = hasheaElString(codigo);
        return this.hashStringSubmission;
    }

    public String hasheaElString(String string) {
        return Hashing.sha256().hashString(string, StandardCharsets.UTF_8).toString();
    }

    @Override
    public String toString() {
        return codigo + language.getNombreLenguaje();
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

    public Set<Result> getResults() {
        return results;
    }

    public void setResults(Set<Result> results) {
        this.results = results;
    }

    public boolean isCorregido() {
        return corregido;
    }

    public void setCorregido(boolean correjido) {
        this.corregido = correjido;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.hashStringDelProblem = problem.getHashString();
        this.problem = problem;
    }

    public void generaHashProblem() {
        this.hashStringDelProblem = problem.getHashString();
    }

    public void addResult(Result res) {
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

    public String getHashStringDelProblem() {
        return hashStringDelProblem;
    }

    public void setHashStringDelProblem(String hashStringDelProblema) {
        this.hashStringDelProblem = hashStringDelProblema;
    }

    public void sumarResultCorregido() {
        this.numeroResultCorregidos++;
    }

    public boolean isTerminadoDeEjecutarResults() {
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

    public String getEsProblemValidatorResultEsperado() {
        return esProblemValidatorResultEsperado;
    }

    public void setEsProblemValidatorResultEsperado(String esProblemValidatorResultadoEsperado) {
        this.esProblemValidatorResultEsperado = esProblemValidatorResultadoEsperado;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return id == that.getId() && problem.equals(that.getProblem()) && team.equals(that.getTeam());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, problem, team);
    }
}