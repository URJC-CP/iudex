package com.example.aplicacion.pojos;

import com.example.aplicacion.entities.Submission;

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
