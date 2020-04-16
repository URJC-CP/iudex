package com.example.aplicacion.Entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String nombreEjercicio;

    @Lob
    @ElementCollection
    private List<String>  entradaOculta;

    @Lob
    @ElementCollection
    private List<String>  entradaVisible;

    @Lob
    @ElementCollection
    private List<String>  salidaOculta;

    @Lob
    @ElementCollection
    private List<String>  salidaVisible;

    @Lob
    @ElementCollection
    private List<String> codigoCorrecto;


    public Problem() {
    }

    public Problem(String nombreEjercicio, List<String> entradaOculta, List<String> salidaOculta, List<String> codigoCorrecto, List<String>  entradaVisible, List<String>  salidaVisible) {
        this.nombreEjercicio = nombreEjercicio;
        this.entradaOculta = entradaOculta;
        this.salidaOculta = salidaOculta;
        this.codigoCorrecto = codigoCorrecto;
        this.entradaVisible = entradaVisible;
        this.salidaVisible = salidaVisible;
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


    public List<String> getEntradaOculta() {
        return entradaOculta;
    }

    public void setEntradaOculta(List<String> entradaOculta) {
        this.entradaOculta = entradaOculta;
    }

    public List<String> getCodigoCorrecto() {
        return codigoCorrecto;
    }

    public void setCodigoCorrecto(List<String> codigoCorrecto) {
        this.codigoCorrecto = codigoCorrecto;
    }

    public List<String> getEntradaVisible() {
        return entradaVisible;
    }

    public void setEntradaVisible(List<String> entradaVisible) {
        this.entradaVisible = entradaVisible;
    }

    public List<String> getSalidaOculta() {
        return salidaOculta;
    }

    public void setSalidaOculta(List<String> salidaOculta) {
        this.salidaOculta = salidaOculta;
    }

    public List<String> getSalidaVisible() {
        return salidaVisible;
    }

    public void setSalidaVisible(List<String> salidaVisible) {
        this.salidaVisible = salidaVisible;
    }
}
