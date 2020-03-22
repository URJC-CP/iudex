package com.example.aplicacion.Entities;

import javax.persistence.*;

@Entity
public class Exercises {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String nombreEjercicio;

    @Lob
    private String  salidaCorrecta;
    @Lob
    private String codigoCorrecto;



}
