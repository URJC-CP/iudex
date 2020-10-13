package com.example.aplicacion.Entities;

import com.example.aplicacion.Pojos.LanguageAPI;

import javax.persistence.*;

@Entity
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true)
    private String nombreLenguaje;

    private String imgenId;

    public Language() {    }

    public Language(String lenguaje, String imgenId) {
        this.nombreLenguaje = lenguaje; this.imgenId = imgenId;
    }
    public LanguageAPI toLanguageAPI(){
        LanguageAPI languageAPI = new LanguageAPI();
        //RELLENAR




        return languageAPI;
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
