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
    @Autowired
    private ProblemService problemService;

    public String creaConcurso(String nameConcurso, String teamId){
        if(concursoRepository.existsByNombreConcurso(nameConcurso)){
            return "concurso NAME DUPLICATED";
        }

        Concurso concurso = new Concurso();
        concurso.setNombreConcurso(nameConcurso);

        Team team =teamRepository.findTeamById(Long.valueOf(teamId));
        if (team == null) {
            return "TEAM NOT FOUND";
        }
        concurso.setTeamPropietario(team);


        concursoRepository.save(concurso);

        return "OK";
    }


    public String deleteConcurso(String idconcurso){
        Concurso concurso = concursoRepository.findConcursoById(Long.valueOf(idconcurso));
        if(concurso==null){
            return "concurso NOT FOUND";
        }
        //buscamos si en la lista de problemas hay alguno que solo este en este concurso
        for(Problem problemAux: concurso.getListaProblemas()){
            //Si hay algun problema que solo pertece a un concurso lo borramos
            if(problemAux.getListaConcursosPertenece().size()==1 &&problemAux.getListaConcursosPertenece().contains(concurso)){
                problemService.deleteProblem(Long.toString(problemAux.getId()));
            }
        }
        concursoRepository.delete(concurso);

        return "OK";
    }

    public String anyadeProblemaConcurso(String idConcurso, String idProblema){

        Concurso concurso = concursoRepository.findConcursoById(Long.valueOf(idConcurso));
        Problem problema = problemRepository.findProblemById(Long.valueOf(idProblema));
        if(concurso==null){
            return "concurso NOT FOUND";
        }
        if (problema==null){
            return "USER NOT FOUND";
        }
        if(concurso.getListaProblemas().contains(problema)){
            return "PROBLEM DUPLICATED";
        }
        concurso.addProblem(problema);
        concursoRepository.save(concurso);

        return "OK";
    }

    public String deleteProblemFromConcurso(String idConcurso, String idProblema){

        Concurso concurso = concursoRepository.findConcursoById(Long.valueOf(idConcurso));
        Problem problema = problemRepository.findProblemById(Long.valueOf(idProblema));
        if(concurso==null){
            return "concurso NOT FOUND";
        }
        if (problema==null){
            return "USER NOT FOUND";
        }
        if(!concurso.getListaProblemas().contains(problema)){
            return "PROBLEM NOT IN CONCURSO";
        }
        concurso.deleteProblem(problema);
        concursoRepository.save(concurso);


        return "OK";
    }

    public String addTeamToconcurso(String idTeam, String idconcurso){
        Concurso concurso = concursoRepository.findConcursoById(Long.valueOf(idconcurso));
        Team team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if(concurso==null){
            return "concurso NOT FOUND";
        }
        if (team==null){
            return "USER NOT FOUND";
        }
        else {
            if(!concurso.getListaParticipantes().contains(team)){
                concurso.addTeam(team);
                concursoRepository.save(concurso);

            }
            else {
                return "YA ESTA EN EL CONCURSO";
            }
        }
        return "OK";
    }


    public String deleteTeamFromconcurso(String idconcurso, String idTeam){
        Concurso concurso = concursoRepository.findConcursoById(Long.valueOf(idconcurso));
        Team team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if(concurso==null){
            return "concurso NOT FOUND";
        }
        if (team==null){
            return "USER NOT FOUND";
        }
        else {
            if(!concurso.getListaParticipantes().contains(team)){
                return "NO ESTA EN EL CONCURSO";
            }
            else {
                concurso.deleteTeam(team);
                concursoRepository.save(concurso);
            }
        }
        return "OK";
    }


    public Concurso getConcurso(String idConcurso){
        return concursoRepository.findConcursoById(Long.valueOf(idConcurso));
    }
    public List<Concurso> getAllConcursos(){
        return concursoRepository.findAll();
    }

}
