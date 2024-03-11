package es.urjc.etsii.grafo.iudex.pojos;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

public class ProblemScore {
    private final ProblemAPI problem;
    private boolean solved;
    private boolean first;
    private float score;
    private int tries;
    private long timestamp;

    public ProblemScore(@NotNull ProblemAPI problem) {
        this(problem, 0, 0L, false, false);
    }

    public ProblemScore(@NotNull ProblemAPI problem, int tries, long timestamp, boolean isFirst, boolean solved) {
        this.problem = problem;
        this.tries = tries;
        this.timestamp = timestamp;
        this.first = isFirst;
        this.solved = solved;
    }

    public ProblemAPI getProblem() {
        return problem;
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

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
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
        return problem.equals(that.problem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problem);
    }
}
