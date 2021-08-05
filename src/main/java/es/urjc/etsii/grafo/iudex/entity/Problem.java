package es.urjc.etsii.grafo.iudex.entity;

import com.google.common.hash.Hashing;
import es.urjc.etsii.grafo.iudex.pojo.ProblemAPI;
import es.urjc.etsii.grafo.iudex.pojo.SampleAPI;
import es.urjc.etsii.grafo.iudex.pojo.SubmissionAPI;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Entity
public class Problem {
    //DEFAULT VALUES
    public static final String TIMEOUT_PROPERTIES = "10";
    public static final String MEMORY_LIMIT_PROPERTIES = "1000";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String nombreEjercicio;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Sample> datos;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<SubmissionProblemValidator> submissionProblemValidators;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<SubmissionProblemValidator> oldSubmissionProblemValidators;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "problema")
    private Set<Submission> submissions;
    @ManyToOne
    private Team equipoPropietario;
    @ManyToMany(mappedBy = "listaProblemasParticipados")
    private Set<Team> listaEquiposIntentados;
    @ManyToMany(mappedBy = "listaProblemas")
    private Set<Contest> listaContestsPertenece;
    private boolean valido;
    private long timestamp = Instant.now().toEpochMilli();
    private int numeroSubmissions;
    private String timeout;
    private String memoryLimit;
    private String autor;
    private String source;
    private String sourceURL;
    private String license;
    private String ownerRights;
    private String hashString;
    @Lob
    private byte[] documento;
    private String validation;
    private String validationFlags;
    private String limitTimeMultiplier;
    private String limitTimeSafetyMargin;
    private String limitMemory;
    private String limitOutput;
    private String limitCode;
    private String limitCompilationTime;
    private String limitCompilationMemory;
    private String limitValidationTime;
    private String limitValidationMemory;
    private String limitValidationOutput;
    private String color;

    public Problem() {
        this.datos = new HashSet<>();
        this.submissionProblemValidators = new HashSet<>();
        this.submissions = new HashSet<>();
        this.listaContestsPertenece = new HashSet<>();
        this.oldSubmissionProblemValidators = new HashSet<>();
        this.listaEquiposIntentados = new HashSet<>();
        //valores por defecto
        if (timeout == null) {
            this.timeout = TIMEOUT_PROPERTIES;
        }
        if (memoryLimit == null) {
            this.memoryLimit = MEMORY_LIMIT_PROPERTIES;
        }
    }

    public Problem(String nombreEjercicio, List<Sample> datosVisibles, List<Sample> datosOcultos) {
        this.nombreEjercicio = nombreEjercicio;
        this.datos = new HashSet<>();
        this.datos.addAll(datosVisibles);
        this.datos.addAll(datosOcultos);

        this.submissionProblemValidators = new HashSet<>();
        this.submissions = new HashSet<>();
        this.listaContestsPertenece = new HashSet<>();
        this.oldSubmissionProblemValidators = new HashSet<>();
        this.listaEquiposIntentados = new HashSet<>();

        this.timeout = TIMEOUT_PROPERTIES;
        this.memoryLimit = MEMORY_LIMIT_PROPERTIES;
        this.hashString = hasheaElString(nombreEjercicio + listaToString(datos));
    }

    public ProblemAPI toProblemAPI() {
        ProblemAPI problemAPI = new ProblemAPI();
        problemAPI.setId(this.id);
        problemAPI.setNombreEjercicio(this.nombreEjercicio);
        problemAPI.setSamples(convertSampletoSampleAPI(this.getDatosVisibles()));

        List<SubmissionAPI> submissionAPIS = new ArrayList<>();
        for (Submission submission : this.submissions) {
            submissionAPIS.add(submission.toSubmissionAPISimple());
        }
        problemAPI.setSubmissions(submissionAPIS);
        problemAPI.setEquipoPropietario(this.equipoPropietario.toTeamAPISimple());
        problemAPI.setValido(this.valido);
        problemAPI.setTimeout(this.timeout);
        problemAPI.setMemoryLimit(this.memoryLimit);
        problemAPI.setAutor(this.autor);
        problemAPI.setSource(this.source);
        problemAPI.setSourceURL(this.sourceURL);
        problemAPI.setLicense(this.license);
        problemAPI.setOwnerRights(this.ownerRights);
        problemAPI.setColor(this.color);
        problemAPI.setTimeout(this.timeout);

        return problemAPI;
    }

    public ProblemAPI toProblemAPIFull() {
        ProblemAPI problemAPI = new ProblemAPI();
        problemAPI.setId(this.id);
        problemAPI.setNombreEjercicio(this.nombreEjercicio);
        problemAPI.setSamples(convertSampletoSampleAPI(this.getDatosVisibles()));

        List<SubmissionAPI> submissionAPIS = new ArrayList<>();
        for (Submission submission : this.submissions) {
            submissionAPIS.add(submission.toSubmissionAPI());
        }
        problemAPI.setSubmissions(submissionAPIS);
        problemAPI.setEquipoPropietario(this.equipoPropietario.toTeamAPISimple());
        problemAPI.setValido(this.valido);
        problemAPI.setTimeout(this.timeout);
        problemAPI.setMemoryLimit(this.memoryLimit);
        problemAPI.setAutor(this.autor);
        problemAPI.setSource(this.source);
        problemAPI.setSourceURL(this.sourceURL);
        problemAPI.setLicense(this.license);
        problemAPI.setOwnerRights(this.ownerRights);
        problemAPI.setColor(this.color);
        problemAPI.setTimeout(this.timeout);

        return problemAPI;
    }

    public ProblemAPI toProblemAPISimple() {
        ProblemAPI problemAPI = new ProblemAPI();
        problemAPI.setId(this.id);
        problemAPI.setNombreEjercicio(this.nombreEjercicio);
        return problemAPI;
    }

    private String listaToString(Set<Sample> lista) {
        String salida = "";
        for (Sample inout : lista) {
            salida = salida.concat(inout.toString());
        }
        return salida;
    }

    private List<SampleAPI> convertSampletoSampleAPI(Set<Sample> samples) {
        List<SampleAPI> salida = new ArrayList<>();
        for (Sample sample : samples) {
            salida.add(sample.toSampleAPI());
        }
        return salida;
    }

    public String generaHash() {
        return this.hashString = hasheaElString(nombreEjercicio + listaToString(datos));
    }

    public String hasheaElString(String string) {
        return Hashing.sha256().hashString(string, StandardCharsets.UTF_8).toString();
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

    public boolean hasTestCaseFiles() {
        return !datos.isEmpty();
    }

    public Set<Sample> getData() {
        return datos;
    }

    public void setData(Set<Sample> datos) {
        this.datos = datos;
    }

    private Set<Sample> getData(boolean visible) {
        Set<Sample> rep = new HashSet<>();
        for (Sample data : datos) {
            if (data.isPublic() == visible) {
                rep.add(data);
            }
        }
        return rep;
    }

    private void removeData(boolean visible) {
        datos.removeIf(sample -> sample.isPublic() == visible);
    }

    public void clearData() {
        this.datos.clear();
    }

    public Set<Sample> getDatosOcultos() {
        return getData(false);
    }

    public void setDatosOcultos(List<Sample> datosOcultos) {
        removeData(false);
        this.datos.addAll(datosOcultos);
    }

    public Set<Sample> getDatosVisibles() {
        return getData(true);
    }

    public void setDatosVisibles(Set<Sample> datosVisibles) {
        removeData(true);
        this.datos.addAll(datosVisibles);
    }

    public void addData(Sample data) {
        datos.add(data);
    }

    public void removeData(Sample data) {
        datos.remove(data);
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

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getValidationFlags() {
        return validationFlags;
    }

    public void setValidationFlags(String validationFlags) {
        this.validationFlags = validationFlags;
    }

    public String getLimitMemory() {
        return limitMemory;
    }

    public void setLimitMemory(String limitMemory) {
        this.limitMemory = limitMemory;
    }

    public String getLimitOutput() {
        return limitOutput;
    }

    public void setLimitOutput(String limitOutput) {
        this.limitOutput = limitOutput;
    }

    public String getLimitCode() {
        return limitCode;
    }

    public void setLimitCode(String limitCode) {
        this.limitCode = limitCode;
    }

    public String getLimitCompilationTime() {
        return limitCompilationTime;
    }

    public void setLimitCompilationTime(String limitCompilationTime) {
        this.limitCompilationTime = limitCompilationTime;
    }

    public String getLimitCompilationMemory() {
        return limitCompilationMemory;
    }

    public void setLimitCompilationMemory(String limitCompilationMemory) {
        this.limitCompilationMemory = limitCompilationMemory;
    }

    public String getLimitValidationTime() {
        return limitValidationTime;
    }

    public void setLimitValidationTime(String limitValidationTime) {
        this.limitValidationTime = limitValidationTime;
    }

    public String getLimitValidationMemory() {
        return limitValidationMemory;
    }

    public void setLimitValidationMemory(String limitValidationMemory) {
        this.limitValidationMemory = limitValidationMemory;
    }

    public String getLimitValidationOutput() {
        return limitValidationOutput;
    }

    public void setLimitValidationOutput(String limitValidationOutput) {
        this.limitValidationOutput = limitValidationOutput;
    }

    public String getLimitTimeMultiplier() {
        return limitTimeMultiplier;
    }

    public void setLimitTimeMultiplier(String limitTimeMultiplier) {
        this.limitTimeMultiplier = limitTimeMultiplier;
    }

    public String getLimitTimeSafetyMargin() {
        return limitTimeSafetyMargin;
    }

    public void setLimitTimeSafetyMargin(String limitTimeSafetyMargin) {
        this.limitTimeSafetyMargin = limitTimeSafetyMargin;
    }

    public Set<SubmissionProblemValidator> getSubmissionProblemValidators() {
        return submissionProblemValidators;
    }

    public void setSubmissionProblemValidators(Set<SubmissionProblemValidator> submissionProblemValidators) {
        this.submissionProblemValidators = submissionProblemValidators;
    }

    public void addSubmissionProblemValidator(SubmissionProblemValidator submissionProblemValidator) {
        this.submissionProblemValidators.add(submissionProblemValidator);
    }

    public void addSubmission(Submission submission) {
        this.submissions.add(submission);
    }

    public Boolean getValido() {
        return valido;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHashString() {
        return hashString;
    }

    public void setHashString(String hashString) {
        this.hashString = hashString;
    }

    public Set<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Submission> submissions) {
        this.submissions = submissions;
    }

    public String getTimeoutPropierties() {
        return TIMEOUT_PROPERTIES;
    }

    public String getMemoryLimitPropierties() {
        return MEMORY_LIMIT_PROPERTIES;
    }

    public Team getEquipoPropietario() {
        return equipoPropietario;
    }

    public void setEquipoPropietario(Team equipoPropietario) {
        this.equipoPropietario = equipoPropietario;
    }

    public Set<Team> getListaEquiposIntentados() {
        return listaEquiposIntentados;
    }

    public void setListaEquiposIntentados(Set<Team> listaEquiposIntentados) {
        this.listaEquiposIntentados = listaEquiposIntentados;
    }

    public byte[] getDocumento() {
        return documento;
    }

    public void setDocumento(byte[] documento) {
        this.documento = documento;
    }

    public Set<Contest> getListaContestsPertenece() {
        return listaContestsPertenece;
    }

    public void setListaContestsPertenece(Set<Contest> listaContestsPertenece) {
        this.listaContestsPertenece = listaContestsPertenece;
    }

    public Set<SubmissionProblemValidator> getOldSubmissionProblemValidators() {
        return oldSubmissionProblemValidators;
    }

    public void setOldSubmissionProblemValidators(Set<SubmissionProblemValidator> oldSubmissionProblemValidators) {
        this.oldSubmissionProblemValidators = oldSubmissionProblemValidators;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public int getNumeroSubmissions() {
        return numeroSubmissions;
    }

    public void setNumeroSubmissions(int numeroSubmissions) {
        this.numeroSubmissions = numeroSubmissions;
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
        Problem problem = (Problem) o;
        return id == problem.getId() && nombreEjercicio.equals(problem.getNombreEjercicio());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreEjercicio);
    }
}
