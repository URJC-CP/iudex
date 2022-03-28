package es.urjc.etsii.grafo.iudex.pojos;

import es.urjc.etsii.grafo.iudex.entities.Submission;

public class SubmissionStringResult {
    Submission submission;
    String salida;

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }
}
