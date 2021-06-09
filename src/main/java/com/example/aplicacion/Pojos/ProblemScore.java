package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.sun.istack.NotNull;

import java.time.Instant;
import java.util.Objects;

public class ProblemScore {
    private final Contest contest;
    private final Problem problem;
    private boolean first;
    private float score;
    private int tries;
    private long timestamp;

    public ProblemScore(@NotNull Problem problem, @NotNull Contest contest) {
        this(problem, contest, 0, 0L, false);
    }

    public ProblemScore(@NotNull Problem problem, @NotNull Contest contest, int tries, long timestamp, boolean isFirst) {
        this.problem = problem;
        this.contest = contest;
        this.tries = tries;
        this.timestamp = timestamp;
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

    public void evaluate() {
        long penalty = Integer.toUnsignedLong(20 * 60 * (this.tries - 1));
        this.score = Instant.ofEpochMilli(timestamp).plusSeconds(penalty).toEpochMilli();
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
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
        return contest.equals(that.contest) && problem.equals(that.problem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contest, problem);
    }

    @Override
    public String toString() {
        return "{\"problem\":\"" + problem.getNombreEjercicio() + "\"" + ",\"score\":" + getScore() + ",\"tries\":" + tries + ",\"time\":" + timestamp + ",\"is_first\":" + isFirst() + "}";
    }
}
