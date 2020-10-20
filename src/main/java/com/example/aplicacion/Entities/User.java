package com.example.aplicacion.Entities;


import com.example.aplicacion.Pojos.UserAPI;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column( unique = true)
    private String nickname;

    @Column( unique = true)
    private String email;

    private long timestamp=  Instant.now().toEpochMilli();


    @ManyToMany
    private List<Team> equiposParticipantes;



    public User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
        this.equiposParticipantes = new ArrayList<>();

    }

    public User() {

    }

    public UserAPI toUserAPI(){
        UserAPI userAPI = new UserAPI();
        userAPI.setId(this.id);
        userAPI.setNickname(this.nickname);
        userAPI.setEmail(this.email);
        userAPI.setTimestamp(this.timestamp);
        return userAPI;
    }

    public UserAPI toUserAPISimple() {
        UserAPI userAPI = new UserAPI();
        userAPI.setId(this.id);
        userAPI.setNickname(this.nickname);
        return userAPI;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
