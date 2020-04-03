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
    private List<String>  entrada;

    @Lob
    @ElementCollection
    private List<String>  salidaCorrecta;

    @Lob
    @ElementCollection
    private List<String> codigoCorrecto;


    public Problem() {
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


    public List<String> getEntrada() {
        return entrada;
    }

    public void setEntrada(List<String> entrada) {
        this.entrada = entrada;
    }

    public List<String> getSalidaCorrecta() {
        return salidaCorrecta;
    }

    public void setSalidaCorrecta(List<String> salidaCorrecta) {
        this.salidaCorrecta = salidaCorrecta;
    }

    public List<String> getCodigoCorrecto() {
        return codigoCorrecto;
    }

    public void setCodigoCorrecto(List<String> codigoCorrecto) {
        this.codigoCorrecto = codigoCorrecto;
    }
}
