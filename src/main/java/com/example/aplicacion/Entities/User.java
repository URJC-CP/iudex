package com.example.aplicacion.Entities;


import javax.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String nickname;
    private String email;

    @OneToMany
    private List<Submission> listaDeSubmissions;

    @OneToMany
    private List<Problem> listaDeProblemasPropios;



}
