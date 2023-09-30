package es.urjc.etsii.grafo.iudex.events.types;

import es.urjc.etsii.grafo.iudex.pojos.SubmissionAPI;

public class IudexSubmissionEvent implements IudexEvent {

    private SubmissionAPI submission;

    public IudexSubmissionEvent() {
    }

    public IudexSubmissionEvent(SubmissionAPI submission) {
        this.submission = submission;
    }

    public SubmissionAPI getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionAPI submission) {
        this.submission = submission;
    }
}
