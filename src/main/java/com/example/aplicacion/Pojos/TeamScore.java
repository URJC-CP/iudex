package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.sun.istack.NotNull;

import java.util.*;

public class TeamScore {
    private final Team team;
    private final Contest contest;
    private Map<Problem, ProblemScore> scoreMap;
    private float score;
    private int solvedProblems;

    public TeamScore(@NotNull Team team, @NotNull Contest contest) {
        this(team, contest, new HashMap<>(), 0, 0f);
    }

    public TeamScore(@NotNull Team team, @NotNull Contest contest, HashMap<Problem, ProblemScore> scoreMap, int solvedProblems, float score) {
        this.team = team;
        this.contest = contest;
        this.scoreMap = scoreMap;
        this.score = score;
        this.solvedProblems = solvedProblems;
    }

    public Team getTeam() {
        return team;
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

    public int getSolvedProblems() {
        return solvedProblems;
    }

    public ProblemScore getProblemScore(Problem problem) {
        ProblemScore ps = scoreMap.getOrDefault(problem, new ProblemScore(problem, contest));
        return ps;
    }

    public List<ProblemScore> getScoreList() {
        return Collections.unmodifiableList(new LinkedList<>(scoreMap.values()));
    }

    public void setScoreMap(Map<Problem, ProblemScore> scoreMap) {
        this.scoreMap = scoreMap;
    }

    public void addProblemScore(ProblemScore score) {
        if (score == null) {
            throw new RuntimeException("Error! Wrong parameter!");
        }
        if (!contest.equals(score.getContest())) {
            throw new RuntimeException("Error! Problem not in Contest!");
        }
        scoreMap.putIfAbsent(score.getProblem(), score);
    }

    public void updateScore(float score) {
        this.score += score;
        this.solvedProblems += 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamScore teamScore = (TeamScore) o;
        return team.equals(teamScore.team) && contest.equals(teamScore.contest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, contest);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"team_name\":").append(team.getNombreEquipo());
        sb.append(",\"problems_solved\":").append(solvedProblems);
        sb.append(",\"score\":").append(score);

        sb.append(",\"problems_scores\":{");
        boolean removeComa = false;
        for (ProblemScore ps : scoreMap.values()) {
            sb.append(ps).append(",");
            if (!removeComa) {
                removeComa = true;
            }
        }
        if (removeComa) {
            sb.setCharAt(sb.lastIndexOf(","), ' ');
        }
        sb.append("}");
        return sb.toString();
    }
}
