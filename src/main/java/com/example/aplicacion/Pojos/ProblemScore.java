package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.sun.istack.NotNull;

public class ProblemScore {
    private Problem problem;
    private Team team;
    private int intentos;
    private long timestamp;
    boolean first;

    public ProblemScore(@NotNull Problem problem, @NotNull Team team, int intentos, long timestamp, boolean isFirst){
        this.problem = problem;
        this.team = team;
        this.intentos = intentos;
        this.timestamp = timestamp;
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
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(problem.getNombreEjercicio()).append("\":{");
        sb.append("\"team:\"").append(team.getId());
        sb.append(",\"num_try:\"").append(intentos);
        sb.append(",\"tiempo:\"").append(timestamp);
        sb.append(",\"is_first\"").append(isFirst());
        sb.append("}");
        return sb.toString();
    }
}
