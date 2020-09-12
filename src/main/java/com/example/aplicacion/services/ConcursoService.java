package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Concurso;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Repository.ConcursoRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConcursoService {
    @Autowired
    private ConcursoRepository concursoRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ProblemRepository problemRepository;

    public String creaConcurso(String nameConcurso, String teamName){
        if(concursoRepository.existsByNombreConcurso(nameConcurso)){
            return "COMPETITION NAME DUPLICATED";
        }

        Concurso concurso = new Concurso();
        concurso.setNombreConcurso(nameConcurso);

        Team team =teamRepository.findByNombreEquipo(teamName);
        if (team == null) {
            return "TEAM NOT FOUND";
        }
        concurso.setTeamPropietario(team);


        concursoRepository.save(concurso);

        return "OK";
    }

    public String anyadeProblemaConcurso(String idConcurso, String idProblema){

        Optional<Concurso> concurso = concursoRepository.findById(Long.getLong(idConcurso));
        Optional<Problem> problema = problemRepository.findById(Long.getLong(idProblema));



        return "OK";
    }
}
