package com.example.aplicacion.Entities;


import javax.persistence.*;
import java.util.ArrayList;
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
    private List<Problem> listaProblemasIntentados;

    @OneToMany
    private List<Problem> listaProblemasCreados;

    public User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
        this.listaDeSubmissions = new ArrayList<>();
        this.listaProblemasCreados = new ArrayList<>();
        this.listaProblemasIntentados = new ArrayList<>();

    }

    public User() {
        this.listaDeSubmissions = new ArrayList<>();
        this.listaProblemasCreados = new ArrayList<>();
        this.listaProblemasIntentados = new ArrayList<>();
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Submission> getListaDeSubmissions() {
        return listaDeSubmissions;
    }

    public void setListaDeSubmissions(List<Submission> listaDeSubmissions) {
        this.listaDeSubmissions = listaDeSubmissions;
    }

    public List<Problem> getListaProblemasIntentados() {
        return listaProblemasIntentados;
    }

    public void setListaProblemasIntentados(List<Problem> listaProblemasIntentados) {
        this.listaProblemasIntentados = listaProblemasIntentados;
    }

    public List<Problem> getListaProblemasCreados() {
        return listaProblemasCreados;
    }

    public void setListaProblemasCreados(List<Problem> listaProblemasCreados) {
        this.listaProblemasCreados = listaProblemasCreados;
    }

    public void addProblemaCreado(Problem problem){
        this.listaProblemasCreados.add(problem);
    }
    public void addProblemaIntentado(Problem problem){
        this.listaProblemasIntentados.add(problem);
    }


}
