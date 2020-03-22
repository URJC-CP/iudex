package com.example.aplicacion.Entities;

import javax.persistence.*;

@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String nombreEjercicio;

    @Lob
    private String  salidaCorrecta;
    @Lob
    private String codigoCorrecto;


    public Exercise() {
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

    public String getSalidaCorrecta() {
        return salidaCorrecta;
    }

    public void setSalidaCorrecta(String salidaCorrecta) {
        this.salidaCorrecta = salidaCorrecta;
    }

    public String getCodigoCorrecto() {
        return codigoCorrecto;
    }

    public void setCodigoCorrecto(String codigoCorrecto) {
        this.codigoCorrecto = codigoCorrecto;
    }
}
