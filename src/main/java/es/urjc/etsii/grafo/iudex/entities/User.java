package es.urjc.etsii.grafo.iudex.entities;


import es.urjc.etsii.grafo.iudex.pojos.UserAPI;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    private String name = "";

    private String familyName = "";

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    private long timestamp = Instant.now().toEpochMilli();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<TeamUser> equiposParticipantes;


    public User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
        this.equiposParticipantes = new HashSet<>();
    }


    public User(String nickname, String email, String name, String familyName) {
        this.nickname = nickname;
        this.email = email;
        this.name = name;
        this.familyName = familyName;
        this.equiposParticipantes = new HashSet<>();
        this.roles = new ArrayList<>();
    }

    public User() {}

    public UserAPI toUserAPI() {
        UserAPI userAPI = new UserAPI();
        userAPI.setId(this.id);
        userAPI.setNickname(this.nickname);
        userAPI.setName(this.name);
        userAPI.setEmail(this.email);
        userAPI.setTimestamp(this.timestamp);
        userAPI.setRoles(new ArrayList<>(this.roles));
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void addTeam(TeamUser team) {
        this.equiposParticipantes.add(team);
    }

    public Set<TeamUser> getEquiposParticipantes() {
        return equiposParticipantes;
    }

    public void setEquiposParticipantes(Set<TeamUser> equiposParticipantes) {
        this.equiposParticipantes = equiposParticipantes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && nickname.equals(user.nickname) && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, email);
    }
}
