package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.InNOut;
import com.example.aplicacion.Entities.Submission;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemAPI {
    private long id;

    private String nombreEjercicio;
    private List<InNOutAPI> entradaVisible;
    private List<InNOutAPI>  salidaVisible;


    private List<SubmissionAPI> submissions;

    private TeamAPI equipoPropietario;

    private Boolean valido;

    private String timeout;
    private String memoryLimit;

    private String autor;
    private String source;
    private String source_url;
    private String license;
    private String rights_owner;



    private String color;

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

    public List<InNOutAPI> getEntradaVisible() {
        return entradaVisible;
    }

    public void setEntradaVisible(List<InNOutAPI> entradaVisible) {
        this.entradaVisible = entradaVisible;
    }

    public List<InNOutAPI> getSalidaVisible() {
        return salidaVisible;
    }

    public void setSalidaVisible(List<InNOutAPI> salidaVisible) {
        this.salidaVisible = salidaVisible;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
