package com.example.aplicacion.Entities;

import javax.persistence.*;

@Entity
public class LanguageAPI {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true)
    private String nombreLenguaje;

    private String imgenId;

    public LanguageAPI() {    }

    public LanguageAPI(String lenguaje, String imgenId) {
        this.nombreLenguaje = lenguaje; this.imgenId = imgenId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreLenguaje() {
        return nombreLenguaje;
    }

    public void setNombreLenguaje(String nombreLenguaje) {
        this.nombreLenguaje = nombreLenguaje;
    }

    public String getImgenId() {
        return imgenId;
    }

    public void setImgenId(String imgenId) {
        this.imgenId = imgenId;
    }
}
