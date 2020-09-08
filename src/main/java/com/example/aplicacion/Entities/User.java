package com.example.aplicacion.Entities;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="nickname", unique = true)
    private String nickname;

    @Column(name="email", unique = true)
    private String email;



    @ManyToMany
    private List<Team> equiposParticipantes;



    public User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
        this.equiposParticipantes = new ArrayList<>();

    }

    public User() {

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



   public void addTeam(Team team){
        this.equiposParticipantes.add(team);
   }

    public List<Team> getEquiposParticipantes() {
        return equiposParticipantes;
    }

    public void setEquiposParticipantes(List<Team> equiposParticipantes) {
        this.equiposParticipantes = equiposParticipantes;
    }
}
