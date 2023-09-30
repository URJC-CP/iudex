package es.urjc.etsii.grafo.iudex.entities;

import es.urjc.etsii.grafo.iudex.pojos.ResultAPI;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

//Clase que guarda cada uno de los intentos dentro de una submision. Un result por cada entrada y salida esperada.
@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    private String codigo;

    @ManyToOne
    private Sample sample;

    @Lob
    private String salidaEstandar;
    @Lob
    private String salidaError;
    @Lob
    private String salidaCompilador;

    private long timestamp;

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
        this(null, null, null, null, "10", "100");
    }

    public Result(Sample datos, String codigo, Language language, String fileName, String maxtimeout, String maxMemory) {
        this.codigo = codigo;
        this.sample = datos;
        this.salidaEstandar = "";
        this.salidaError = "";
        this.salidaCompilador = "";
        this.signalCompilador = "0";
        this.signalEjecutor = "";
        this.resultadoRevision = "";
        this.language = language;
        this.fileName = fileName;
        this.maxTimeout = maxtimeout;
        this.maxMemory = maxMemory;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ResultAPI toResultAPI() {
        ResultAPI resultAPI = new ResultAPI();
        resultAPI.setId(this.id);
        resultAPI.setCodigo(this.codigo);
        resultAPI.setNumeroCasoDePrueba(this.numeroCasoDePrueba);
        resultAPI.setExecTime(this.execTime);
        resultAPI.setExecMemory(this.execMemory);
        resultAPI.setRevisado(this.revisado);
        resultAPI.setResultadoRevision(this.resultadoRevision);
        resultAPI.setLanguage(this.language.toLanguageAPI());
        resultAPI.setTimestamp(this.timestamp);
        return resultAPI;
    }

    public ResultAPI toResultAPISimple() {
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
        return codigo + getEntrada() + salidaCompilador + salidaError + salidaEstandar;
    }

    public String getEntrada() {
        return sample.getInputText();
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample entrada) {
        this.sample = entrada;
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
        return sample.getOutputText();
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
        Result result = (Result) o;
        return id == result.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
