package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.Team;

import java.util.List;

public class ConcursoAPI {
    private long id;

    private String nombreConcurso;
    private TeamAPI teamPropietario;
    private List<ProblemAPI> listaProblemas;
    private List<TeamAPI> listaParticipantes;
    private List<SubmissionAPI> listaSubmissions;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreConcurso() {
        return nombreConcurso;
    }

    public void setNombreConcurso(String nombreConcurso) {
        this.nombreConcurso = nombreConcurso;
    }

}
