package com.example.aplicacion.Entities;

import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Pojos.SampleAPI;
import com.example.aplicacion.Pojos.SubmissionAPI;
import com.google.common.hash.Hashing;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Entity
public class Problem {
    //DEFAULT VALUES
    public final String timeoutPropierties = "10";
    public final String memoryLimitPropierties = "1000";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String nombreEjercicio;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Sample> datos;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SubmissionProblemValidator> submissionProblemValidators;
    @OneToMany(cascade = CascadeType.ALL)
    private List<SubmissionProblemValidator> oldSubmissionProblemValidators;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "problema")
    private List<Submission> submissions;
    @ManyToOne
    private Team equipoPropietario;
    @ManyToMany(mappedBy = "listaProblemasParticipados")
    private List<Team> listaEquiposIntentados;
    @ManyToMany(mappedBy = "listaProblemas")
    private List<Contest> listaContestsPertenece;
    private boolean valido;
    private long timestamp = Instant.now().toEpochMilli();
    private int numeroSubmissions;
    private String timeout;
    private String memoryLimit;
    private String autor;
    private String source;
    private String source_url;
    private String license;
    private String rights_owner;
    //private String keywords;
    private String hashString;
    @Lob
    private byte[] documento;
    private String validation;
    private String validation_flags;
    private String limit_time_multiplier;
    private String limit_time_safety_margin;
    private String limit_memory;
    private String limit_output;
    private String limit_code;
    private String limit_compilation_time;
    private String limit_compilation_memory;
    private String limit_validation_time;
    private String limit_validation_memory;
    private String limit_validation_output;
    private String color;

    public Problem() {
        this.datos = new ArrayList<>();
        this.submissionProblemValidators = new ArrayList<>();
        this.submissions = new ArrayList<>();
        this.listaContestsPertenece = new ArrayList<>();
        this.oldSubmissionProblemValidators = new ArrayList<>();
        this.listaEquiposIntentados = new ArrayList<>();
        //valores por defecto
        if (timeout == null) {
            this.timeout = timeoutPropierties;
        }
        if (memoryLimit == null) {
            this.memoryLimit = memoryLimitPropierties;

        }
    }

    public Problem(String nombreEjercicio, List<Sample> datosVisibles, List<Sample> datosOcultos) {
        this.nombreEjercicio = nombreEjercicio;
        this.datos.addAll(datosVisibles);
        this.datos.addAll(datosOcultos);

        this.submissionProblemValidators = new ArrayList<>();
        this.submissions = new ArrayList<>();
        this.listaContestsPertenece = new ArrayList<>();
        this.oldSubmissionProblemValidators = new ArrayList<>();
        this.listaEquiposIntentados = new ArrayList<>();

        this.timeout = timeoutPropierties;
        this.memoryLimit = memoryLimitPropierties;
        this.hashString = hasheaElString(nombreEjercicio + listaToString(datosOcultos) + listaToString(datosVisibles));
    }

    public ProblemAPI toProblemAPI() {
        ProblemAPI problemAPI = new ProblemAPI();
        problemAPI.setId(this.id);
        problemAPI.setNombreEjercicio(this.nombreEjercicio);
        problemAPI.setSamples(convertInNOuttoInNOUTAPI(this.getDatosVisibles()));

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
        problemAPI.setSource_url(this.source_url);
        problemAPI.setLicense(this.license);
        problemAPI.setRights_owner(this.rights_owner);
        problemAPI.setColor(this.color);
        problemAPI.setTimeout(this.timeout);

        return problemAPI;
    }

    public ProblemAPI toProblemAPIFull() {
        ProblemAPI problemAPI = new ProblemAPI();
        problemAPI.setId(this.id);
        problemAPI.setNombreEjercicio(this.nombreEjercicio);
        problemAPI.setSamples(convertInNOuttoInNOUTAPI(this.getDatosVisibles()));

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
        problemAPI.setSource_url(this.source_url);
        problemAPI.setLicense(this.license);
        problemAPI.setRights_owner(this.rights_owner);
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

    private String listaToString(List<Sample> lista) {
        String salida = "";
        for (Sample inout : lista) {
            salida = salida.concat(inout.toString());
        }
        return salida;
    }

    private List<SampleAPI> convertInNOuttoInNOUTAPI(List<Sample> inNOuts) {
        List<SampleAPI> salida = new ArrayList<>();
        for (Sample inNOut : inNOuts) {
            salida.add(inNOut.toInNOutAPI());
        }
        return salida;
    }

    private List<String> convertListINOtoListString(List<Sample> samples) {
        List<String> salida = new ArrayList<>();
        for (Sample sample : samples) {
            salida.add(sample.getInputText());
            salida.add(sample.getOutputText());
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
        return datos.size() > 0;
    }

    public List<Sample> getData() {
        return datos;
    }

    public void setData(List<Sample> datos) {
        this.datos = datos;
    }

    private List<Sample> getData(boolean visible) {
        Set<Sample> rep = new HashSet<>();
        for (Sample data : datos) {
            if (data.isPublic() == visible) {
                rep.add(data);
            }
        }
        return new ArrayList<>(rep);
    }

    private void removeData(boolean visible) {
        datos.removeIf(data -> data.isPublic() == visible);
    }

    public void clearData() {
        this.datos.clear();
    }

    public List<Sample> getDatosOcultos() {
        return getData(false);
    }

    public void setDatosOcultos(List<Sample> datosOcultos) {
        removeData(false);
        this.datos.addAll(datosOcultos);
    }

    public List<Sample> getDatosVisibles() {
        return getData(true);
    }

    public void setDatosVisibles(List<Sample> datosVisibles) {
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

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getRights_owner() {
        return rights_owner;
    }

    public void setRights_owner(String rights_owner) {
        this.rights_owner = rights_owner;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getValidation_flags() {
        return validation_flags;
    }

    public void setValidation_flags(String validation_flags) {
        this.validation_flags = validation_flags;
    }

    public String getLimit_memory() {
        return limit_memory;
    }

    public void setLimit_memory(String limit_memory) {
        this.limit_memory = limit_memory;
    }

    public String getLimit_output() {
        return limit_output;
    }

    public void setLimit_output(String limit_output) {
        this.limit_output = limit_output;
    }

    public String getLimit_code() {
        return limit_code;
    }

    public void setLimit_code(String limit_code) {
        this.limit_code = limit_code;
    }

    public String getLimit_compilation_time() {
        return limit_compilation_time;
    }

    public void setLimit_compilation_time(String limit_compilation_time) {
        this.limit_compilation_time = limit_compilation_time;
    }

    public String getLimit_compilation_memory() {
        return limit_compilation_memory;
    }

    public void setLimit_compilation_memory(String limit_compilation_memory) {
        this.limit_compilation_memory = limit_compilation_memory;
    }

    public String getLimit_validation_time() {
        return limit_validation_time;
    }

    public void setLimit_validation_time(String limit_validation_time) {
        this.limit_validation_time = limit_validation_time;
    }

    public String getLimit_validation_memory() {
        return limit_validation_memory;
    }

    public void setLimit_validation_memory(String limit_validation_memory) {
        this.limit_validation_memory = limit_validation_memory;
    }

    public String getLimit_validation_output() {
        return limit_validation_output;
    }

    public void setLimit_validation_output(String limit_validation_output) {
        this.limit_validation_output = limit_validation_output;
    }

    public String getLimit_time_multiplier() {
        return limit_time_multiplier;
    }

    public void setLimit_time_multiplier(String limit_time_multiplier) {
        this.limit_time_multiplier = limit_time_multiplier;
    }

    public String getLimit_time_safety_margin() {
        return limit_time_safety_margin;
    }

    public void setLimit_time_safety_margin(String limit_time_safety_margin) {
        this.limit_time_safety_margin = limit_time_safety_margin;
    }

    public List<SubmissionProblemValidator> getSubmissionProblemValidators() {
        return submissionProblemValidators;
    }

    public void setSubmissionProblemValidators(List<SubmissionProblemValidator> submissionProblemValidators) {
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

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public String getTimeoutPropierties() {
        return timeoutPropierties;
    }

    public String getMemoryLimitPropierties() {
        return memoryLimitPropierties;
    }

    public Team getEquipoPropietario() {
        return equipoPropietario;
    }

    public void setEquipoPropietario(Team equipoPropietario) {
        this.equipoPropietario = equipoPropietario;
    }

    public List<Team> getListaEquiposIntentados() {
        return listaEquiposIntentados;
    }

    public void setListaEquiposIntentados(List<Team> listaEquiposIntentados) {
        this.listaEquiposIntentados = listaEquiposIntentados;
    }

    public byte[] getDocumento() {
        return documento;
    }

    public void setDocumento(byte[] documento) {
        this.documento = documento;
    }

    public List<Contest> getListaContestsPertenece() {
        return listaContestsPertenece;
    }

    public void setListaContestsPertenece(List<Contest> listaContestsPertenece) {
        this.listaContestsPertenece = listaContestsPertenece;
    }

    public List<SubmissionProblemValidator> getOldSubmissionProblemValidators() {
        return oldSubmissionProblemValidators;
    }

    public void setOldSubmissionProblemValidators(List<SubmissionProblemValidator> oldSubmissionProblemValidators) {
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
