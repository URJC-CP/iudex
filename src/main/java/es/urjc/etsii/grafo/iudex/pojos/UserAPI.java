package es.urjc.etsii.grafo.iudex.pojos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserAPI {

    private long id;
    private String nickname;
    private String email;

    private String name;

    private String familyName;
    private long timestamp;

    private List<String> roles;

    private String rolesString;

    private Optional<Integer> submissions = Optional.empty();

    private Optional<Integer> contestsParticipated = Optional.empty();

    private Optional<Integer> acceptedSubmissions = Optional.empty();


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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
        this.rolesString = String.join(" ", roles);
    }

    public int getSubmissions() {
        return submissions.orElse(0);
    }

    public void setSubmissions(int submissions) {
        this.submissions = Optional.of(submissions);
    }

    public int getContestsParticipated() {
        return contestsParticipated.orElse(0);
    }

    public void setContestsParticipated(int contestsParticipated) {
        this.contestsParticipated = Optional.of(contestsParticipated);
    }

    public int getAcceptedSubmissions() {
        return acceptedSubmissions.orElse(0);
    }

    public void setAcceptedSubmissions(int acceptedSubmissions) {
        this.acceptedSubmissions = Optional.of(acceptedSubmissions);
    }
}
