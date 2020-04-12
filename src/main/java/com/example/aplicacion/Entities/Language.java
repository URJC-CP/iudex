package com.example.aplicacion.Entities;

import javax.persistence.*;

@Entity
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true)
    private String nombreLenguaje;

    public Language() {    }

    public Language(String lenguaje) {
        this.nombreLenguaje = lenguaje;
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
}
