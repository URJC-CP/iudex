package com.example.aplicacion.Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String nombreEjercicio;


    @OneToMany(cascade = CascadeType.MERGE)
    private List<InNOut>  entradaOculta;

    @OneToMany(cascade = CascadeType.MERGE)
    private List<InNOut>  entradaVisible;

    @OneToMany(cascade = CascadeType.MERGE)
    private List<InNOut>  salidaOculta;

    @OneToMany(cascade = CascadeType.MERGE)
    private List<InNOut>  salidaVisible;

    @OneToMany(cascade = CascadeType.MERGE)
    private List<InNOut> codigoCorrecto;


    public Problem() {
        this.entradaVisible = new ArrayList<>();
        this.salidaVisible = new ArrayList<>();
        this.entradaOculta = new ArrayList<>();
        this.salidaOculta = new ArrayList<>();
    }

    public Problem(String nombreEjercicio, List<InNOut> entradaOculta, List<InNOut> salidaOculta, List<InNOut> codigoCorrecto, List<InNOut>  entradaVisible, List<InNOut>  salidaVisible) {
        this.nombreEjercicio = nombreEjercicio;
        this.entradaOculta = entradaOculta;
        this.salidaOculta = salidaOculta;
        this.codigoCorrecto = codigoCorrecto;
        this.entradaVisible = entradaVisible;
        this.salidaVisible = salidaVisible;

        this.entradaVisible = new ArrayList<>();
        this.salidaVisible = new ArrayList<>();
        this.entradaOculta = new ArrayList<>();
        this.salidaOculta = new ArrayList<>();

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


    public List<InNOut> getEntradaOculta() {
        return entradaOculta;
    }

    public void setEntradaOculta(List<InNOut> entradaOculta) {
        this.entradaOculta = entradaOculta;
    }

    public List<InNOut> getCodigoCorrecto() {
        return codigoCorrecto;
    }

    public void setCodigoCorrecto(List<InNOut> codigoCorrecto) {
        this.codigoCorrecto = codigoCorrecto;
    }

    public List<InNOut> getEntradaVisible() {
        return entradaVisible;
    }

    public void setEntradaVisible(List<InNOut> entradaVisible) {
        this.entradaVisible = entradaVisible;
    }

    public List<InNOut> getSalidaOculta() {
        return salidaOculta;
    }

    public void setSalidaOculta(List<InNOut> salidaOculta) {
        this.salidaOculta = salidaOculta;
    }

    public List<InNOut> getSalidaVisible() {
        return salidaVisible;
    }

    public void setSalidaVisible(List<InNOut> salidaVisible) {
        this.salidaVisible = salidaVisible;
    }

    public void addEntradaVisible(InNOut aux){
        this.entradaVisible.add(aux);
    }
}
