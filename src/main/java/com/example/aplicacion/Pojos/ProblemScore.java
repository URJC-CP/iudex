package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.sun.istack.NotNull;

import java.time.Instant;
import java.util.Objects;

public class ProblemScore {
    private boolean first;
    private Problem problem;
    private Team team;
    private float score;
    private int intentos;
    private long timestamp;

    public ProblemScore(@NotNull Problem problem, @NotNull Team team) {
        this(problem, team, 0, Instant.now().toEpochMilli(), false);
    }

    public ProblemScore(@NotNull Problem problem, @NotNull Team team, int intentos, long timestamp, boolean isFirst) {
        this.problem = problem;
        this.team = team;
        this.intentos = intentos;
        this.timestamp = 0;
        this.first = isFirst;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProblemScore that = (ProblemScore) o;
        return problem.equals(that.problem) && team.equals(that.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problem, team);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"problem\":").append(problem.getNombreEjercicio());
        sb.append(",\"score\":").append(getScore());
        sb.append(",\"tries:\"").append(intentos);
        sb.append(",\"time:\"").append(timestamp);
        sb.append(",\"is_first\"").append(isFirst());
        return sb.toString();
    }
}
