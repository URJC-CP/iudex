package es.urjc.etsii.grafo.iudex.pojos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemAPI {
    private long id;
    private String nombreEjercicio;
    private List<SampleAPI> samples;
    private List<SubmissionAPI> submissions;
    private TeamAPI equipoPropietario;

    private Boolean valido;
    private String timeout;
    private String memoryLimit;

    private String autor;
    private String source;
    private String sourceURL;
    private String license;
    private String ownerRights;

    private String color;

    private String problemURLpdf = null;

    private int numContest = 0;

    public void setProblemURLpdf(String problemURLpdf) {
        this.problemURLpdf = problemURLpdf;
    }

    public String getProblemURLpdf() {
        return problemURLpdf;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreEjercicio() {
        return nombreEjercicio;
    }

    public void setNombreEjercicio(String nombreEjercicio) {
        this.nombreEjercicio = nombreEjercicio;
    }

    public List<SubmissionAPI> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<SubmissionAPI> submissions) {
        this.submissions = submissions;
    }

    public TeamAPI getEquipoPropietario() {
        return equipoPropietario;
    }

    public void setEquipoPropietario(TeamAPI equipoPropietario) {
        this.equipoPropietario = equipoPropietario;
    }

    public Boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(String memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getOwnerRights() {
        return ownerRights;
    }

    public void setOwnerRights(String ownerRights) {
        this.ownerRights = ownerRights;
    }

    public List<SampleAPI> getSamples() {
        return samples;
    }

    public void setSamples(List<SampleAPI> samples) {
        this.samples = samples;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getNumContest() {
        return numContest;
    }

    public void setNumContest(int numContest) {
        this.numContest = numContest;
    }
}
