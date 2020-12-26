package com.example.aplicacion.Entities;

import com.example.aplicacion.Pojos.InNOutAPI;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Pojos.SubmissionAPI;
import com.google.common.hash.Hashing;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Problem {
    //DEFAULT VALUES
    public final String timeoutPropierties = "10";
    public final String memoryLimitPropierties = "1000";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombreEjercicio;
    @OneToMany(cascade = CascadeType.ALL)
    private List<InNOut> datos;
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

    public Problem(String nombreEjercicio, List<InNOut> entradaOculta, List<InNOut> salidaOculta, List<InNOut> entradaVisible, List<InNOut> salidaVisible) {
        this.nombreEjercicio = nombreEjercicio;
        datos.addAll(entradaOculta);
        datos.addAll(salidaOculta);
        datos.addAll(entradaVisible);
        datos.addAll(salidaVisible);

        this.submissionProblemValidators = new ArrayList<>();
        this.submissions = new ArrayList<>();
        this.listaContestsPertenece = new ArrayList<>();
        this.oldSubmissionProblemValidators = new ArrayList<>();
        this.listaEquiposIntentados = new ArrayList<>();

        this.timeout = timeoutPropierties;
        this.memoryLimit = memoryLimitPropierties;
        this.hashString = hasheaElString(nombreEjercicio + listaToString(entradaOculta) + listaToString(salidaOculta) + listaToString(entradaVisible) + listaToString(salidaVisible));
    }

    public ProblemAPI toProblemAPI() {
        ProblemAPI problemAPI = new ProblemAPI();
        problemAPI.setId(this.id);
        problemAPI.setNombreEjercicio(this.nombreEjercicio);
        problemAPI.setEntradaVisible(convertInNOuttoInNOUTAPI(this.getEntradaVisible()));
        problemAPI.setSalidaVisible(convertInNOuttoInNOUTAPI(this.getSalidaVisible()));
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
        problemAPI.setEntradaVisible(convertInNOuttoInNOUTAPI(this.getEntradaVisible()));
        problemAPI.setSalidaVisible(convertInNOuttoInNOUTAPI(this.getSalidaVisible()));
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

    private String listaToString(List<InNOut> lista) {
        String salida = "";
        for (InNOut inout : lista) {
            salida.concat(inout.toString());
        }
        return salida;
    }

    private List<InNOutAPI> convertInNOuttoInNOUTAPI(List<InNOut> inNOuts) {
        List<InNOutAPI> salida = new ArrayList<>();
        for (InNOut inNOut : inNOuts) {
            salida.add(inNOut.toInNOutAPI());
        }
        return salida;
    }

    private List<String> convertListINOtoListString(List<InNOut> inNOuts) {
        List<String> salida = new ArrayList<>();
        for (InNOut inNOut : inNOuts) {
            salida.add(inNOut.getText());
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

    public List<InNOut> getData() {
        return datos;
    }

    public void setData(List<InNOut> datos) {
        this.datos = datos;
    }

    private List<InNOut> getData(ProblemDataType type) {
        Set<InNOut> rep = new HashSet<>();
        for (InNOut data : datos) {
            if (data.getType() == type) {
                rep.add(data);
            }
        }
        ArrayList<InNOut> dataFiles = new ArrayList<>();
        dataFiles.addAll(rep);
        return dataFiles;
    }

    private void removeData(ProblemDataType type) {
        for (ProblemData data : datos) {
            if (data.getType() == type) {
                datos.remove(data);
            }
        }
    }

    public List<InNOut> getEntradaOculta() {
        return getData(ProblemDataType.EntradaOculta);
    }

    public void setEntradaOculta(List<InNOut> entradaOculta) {
        removeData(ProblemDataType.EntradaOculta);
        this.datos.addAll(entradaOculta);
    }

    public List<InNOut> getEntradaVisible() {
        return getData(ProblemDataType.EntradaVisible);
    }

    public void setEntradaVisible(List<InNOut> entradaVisible) {
        removeData(ProblemDataType.EntradaVisible);
        this.datos.addAll(entradaVisible);
    }

    public List<InNOut> getSalidaOculta() {
        return getData(ProblemDataType.SalidaOculta);
    }

    public void setSalidaOculta(List<InNOut> salidaOculta) {
        removeData(ProblemDataType.SalidaOculta);
        this.datos.addAll(salidaOculta);
    }

    public List<InNOut> getSalidaVisible() {
        return getData(ProblemDataType.SalidaVisible);
    }

    public void setSalidaVisible(List<InNOut> salidaVisible) {
        removeData(ProblemDataType.SalidaVisible);
        this.datos.addAll(salidaVisible);
    }

    public void addData(InNOut data) {
        datos.add(data);
    }

    public void removeData(InNOut data) {
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
}
