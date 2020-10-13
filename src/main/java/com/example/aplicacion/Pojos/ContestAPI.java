package com.example.aplicacion.Pojos;

import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.Team;

import java.util.List;

public class ContestAPI {
    private long id;

    private String nombreContest;
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

    public String getNombreContest() {
        return nombreContest;
    }

    public void setNombreContest(String nombreContest) {
        this.nombreContest = nombreContest;
    }

}
