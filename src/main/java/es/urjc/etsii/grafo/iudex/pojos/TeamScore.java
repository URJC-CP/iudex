package es.urjc.etsii.grafo.iudex.pojos;

import es.urjc.etsii.grafo.iudex.entities.Problem;

import javax.validation.constraints.NotNull;
import java.util.*;

public class TeamScore {
    private final TeamAPI team;
    private Map<Problem, ProblemScore> scoreMap;
    private float score;
    private int solvedProblems;

    public TeamScore(@NotNull TeamAPI team) {
        this(team, new HashMap<>(), 0, 0f);
    }

    public TeamScore(@NotNull TeamAPI team, Map<Problem, ProblemScore> scoreMap, int solvedProblems, float score) {
        this.team = team;
        this.scoreMap = scoreMap;
        this.score = score;
        this.solvedProblems = solvedProblems;
    }

    public TeamAPI getTeam() {
        return team;
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
        return scoreMap.getOrDefault(problem, new ProblemScore(problem.toProblemAPISimple()));
    }

    public List<ProblemScore> getScoreList() {
        return Collections.unmodifiableList(new LinkedList<>(scoreMap.values()));
    }

    public void setScoreMap(Map<Problem, ProblemScore> scoreMap) {
        this.scoreMap = scoreMap;
    }

    public void addProblemScore(Problem problem, ProblemScore score) {
        if (score == null) {
            throw new RuntimeException("Error! Wrong parameter!");
        }
        scoreMap.putIfAbsent(problem, score);
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
        return team.equals(teamScore.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team);
    }
}
