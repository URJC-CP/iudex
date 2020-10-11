package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Language;

import javax.persistence.Lob;

public class SubmissionAPI {

    private long id;

    private String codigo;
    private String salidaEstandar;
    private String salidaError;
    private String salidaCompilador;

    private String salidaTime;
    //TIMEOUT FILE fromthe container
    private String signalCompilador;
    private String signalEjecutor;
    private float execTime;
    private float execMemory;
    private boolean revisado;
    private String resultadoRevision;

    private Language language;
    private String fileName;


    private String maxMemory;
    private String maxTimeout;

}
