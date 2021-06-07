package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.sun.istack.NotNull;

import java.util.Objects;

public class ProblemScore {
    private final Contest contest;
    private final Problem problem;
    private boolean first;
    private float score;
    private int intentos;
    private float timestamp;

    public ProblemScore(@NotNull Problem problem, @NotNull Contest contest) {
        this(problem, contest, 0, 0f, false);
    }

    public ProblemScore(@NotNull Problem problem, @NotNull Contest contest, int intentos, float timestamp, boolean isFirst) {
        this.problem = problem;
        this.contest = contest;
        this.intentos = intentos;
        this.timestamp = 0;
        this.first = isFirst;
    }

    public Problem getProblem() {
        return problem;
    }

    public Contest getContest() {
        return contest;
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

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(float timestamp) {
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
        return contest.equals(that.contest) && problem.equals(that.problem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contest, problem);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"problem\":\"").append(problem.getNombreEjercicio()).append("\"");
        sb.append(",\"score\":").append(getScore());
        sb.append(",\"tries\":").append(intentos);
        sb.append(",\"time\":").append(timestamp);
        sb.append(",\"is_first\":").append(isFirst());
        sb.append("}");
        return sb.toString();
    }
}
