package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Repository.ContestRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestService {
    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ProblemService problemService;

    public String creaContest(String nameContest, String teamId){
        if(contestRepository.existsByNombreContest(nameContest)){
            return "contest NAME DUPLICATED";
        }

        Contest contest = new Contest();
        contest.setNombreContest(nameContest);

        Team team =teamRepository.findTeamById(Long.valueOf(teamId));
        if (team == null) {
            return "TEAM NOT FOUND";
        }
        contest.setTeamPropietario(team);


        contestRepository.save(contest);

        return "OK";
    }


    public String deleteContest(String idcontest){
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if(contest ==null){
            return "contest NOT FOUND";
        }
        //buscamos si en la lista de problemas hay alguno que solo este en este contest
        boolean borrar = false;
        Problem problemAux2 = new Problem();
        for(Problem problemAux: contest.getListaProblemas()){
            //Si hay algun problema que solo pertece a un contest lo borramos
            if(problemAux.getListaContestsPertenece().size()==1 &&problemAux.getListaContestsPertenece().contains(contest)){
                borrar=true;
                problemAux2 = problemAux;
            }
        }
        if (borrar) {
            problemService.deleteProblem(Long.toString(problemAux2.getId()));
        }

        //borramos el contest
        contestRepository.delete(contest);

        return "OK";
    }

    public String anyadeProblemaContest(String idContest, String idProblema){

        Contest contest = contestRepository.findContestById(Long.valueOf(idContest));
        Problem problema = problemRepository.findProblemById(Long.valueOf(idProblema));
        if(contest ==null){
            return "contest NOT FOUND";
        }
        if (problema==null){
            return "USER NOT FOUND";
        }
        if(contest.getListaProblemas().contains(problema)){
            return "PROBLEM DUPLICATED";
        }
        contest.addProblem(problema);
        contestRepository.save(contest);

        return "OK";
    }

    public String deleteProblemFromContest(String idContest, String idProblema){

        Contest contest = contestRepository.findContestById(Long.valueOf(idContest));
        Problem problema = problemRepository.findProblemById(Long.valueOf(idProblema));
        if(contest ==null){
            return "contest NOT FOUND";
        }
        if (problema==null){
            return "USER NOT FOUND";
        }
        if(!contest.getListaProblemas().contains(problema)){
            return "PROBLEM NOT IN CONCURSO";
        }
        contest.deleteProblem(problema);
        contestRepository.save(contest);


        return "OK";
    }

    public String addTeamTocontest(String idTeam, String idcontest){
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        Team team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if(contest ==null){
            return "contest NOT FOUND";
        }
        if (team==null){
            return "USER NOT FOUND";
        }
        else {
            if(!contest.getListaParticipantes().contains(team)){
                contest.addTeam(team);
                contestRepository.save(contest);

            }
            else {
                return "YA ESTA EN EL CONCURSO";
            }
        }
        return "OK";
    }


    public String deleteTeamFromcontest(String idcontest, String idTeam){
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        Team team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if(contest ==null){
            return "contest NOT FOUND";
        }
        if (team==null){
            return "USER NOT FOUND";
        }
        else {
            if(!contest.getListaParticipantes().contains(team)){
                return "NO ESTA EN EL CONCURSO";
            }
            else {
                contest.deleteTeam(team);
                contestRepository.save(contest);
            }
        }
        return "OK";
    }


    public Contest getContest(String idContest){
        return contestRepository.findContestById(Long.valueOf(idContest));
    }
    public List<Contest> getAllContests(){
        return contestRepository.findAll();
    }

}
