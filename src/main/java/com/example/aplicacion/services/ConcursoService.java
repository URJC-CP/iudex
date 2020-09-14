package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Concurso;
import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Repository.ConcursoRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

        Concurso concurso = concursoRepository.findConcursoById(Long.valueOf(idConcurso));
        Problem problema = problemRepository.findProblemById(Long.valueOf(idProblema));

        concurso.addProblem(problema);
        concursoRepository.save(concurso);


        return "OK";
    }
    public String addTeamTocompetition(String idcompetition, String idTeam){
        Concurso competition = concursoRepository.findConcursoById(Long.valueOf(idcompetition));
        Team team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if(competition==null){
            return "competition NOT FOUND";
        }
        if (team==null){
            return "USER NOT FOUND";
        }
        else {
            competition.addTeam(team);
        }
        return "OK";
    }

    public String borracompetition(String idcompetition){
        Concurso competition = concursoRepository.findConcursoById(Long.valueOf(idcompetition));
        if(competition==null){
            return "competition NOT FOUND";
        }
        concursoRepository.delete(competition);

        return "OK";
    }
    public String deleteTeamFromcompetition(String idcompetition, String idTeam){
        Concurso competition = concursoRepository.findConcursoById(Long.valueOf(idcompetition));
        Team team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if(competition==null){
            return "competition NOT FOUND";
        }
        if (team==null){
            return "USER NOT FOUND";
        }
        else {
            if(!competition.getListaParticipantes().contains(team)){
                competition.addTeam(team);
            }
            else {
                return "YA ESTA EN EL CONCURSO";
            }
        }
        return "OK";
    }
    public List<Concurso> getAllConcursos(){
        return concursoRepository.findAll();
    }

}
