package es.urjc.etsii.grafo.iudex.events.types;

import es.urjc.etsii.grafo.iudex.pojos.ProblemScore;
import es.urjc.etsii.grafo.iudex.pojos.UserAPI;

public class IudexSubmissionEvent implements IudexEvent {

    private final UserAPI user;

    private final ProblemScore problemScore;

    public IudexSubmissionEvent(UserAPI user, ProblemScore problemScore) {
        this.user = user;
        this.problemScore = problemScore;
    }

    public UserAPI getUser() {
        return user;
    }

    public ProblemScore getProblemScore() {
        return problemScore;
    }
}
