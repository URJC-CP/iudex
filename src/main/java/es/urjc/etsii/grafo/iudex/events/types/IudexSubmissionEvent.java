package es.urjc.etsii.grafo.iudex.events.types;

import es.urjc.etsii.grafo.iudex.pojos.SubmissionAPI;

public class IudexSubmissionEvent implements IudexEvent {

    private final SubmissionAPI submission;

    public IudexSubmissionEvent(SubmissionAPI submission) {
        this.submission = submission;
    }

    public SubmissionAPI getSubmission() {
        return submission;
    }
}
