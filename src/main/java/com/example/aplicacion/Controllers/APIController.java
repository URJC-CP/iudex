package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.Concurso;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Pojos.ConcursoAPI;
import com.example.aplicacion.services.ConcursoService;
import com.example.aplicacion.services.SubmissionService;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/API")
@RestController
public class APIController {

    @Autowired
    SubmissionService submissionService;
    @Autowired
    ConcursoService concursoService;


    @GetMapping("/submissions/all")
    public List<Submission> submissionsAll(){
        return submissionService.getAllSubmissions();
    }

    //CONCURSOS
    @GetMapping("/concursos/all")
    public List<ConcursoAPI> concursos(){
        List<Concurso> concursoList = concursoService.getAllConcursos();
        List<ConcursoAPI> concursoAPIS = new ArrayList<>();

        for (Concurso concurso:concursoList){
            concursoAPIS.add(concurso.toConcursoAPI());
        }
        return concursoAPIS;
    }







}
