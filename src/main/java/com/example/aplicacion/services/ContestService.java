package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Pojos.ContestString;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Repository.ContestRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ContestString creaContest(String nameContest, String teamId, Optional<String> descripcion) {
        ContestString salida = new ContestString();
        if (contestRepository.existsByNombreContest(nameContest)) {
            salida.setSalida("contest NAME DUPLICATED");
            return salida;
        }

        Contest contest = new Contest();
        contest.setNombreContest(nameContest);

        if (descripcion.isPresent()) {
            contest.setDescripcion(descripcion.get());
        }
        Optional<Team> team = teamRepository.findTeamById(Long.valueOf(teamId));
        if (team.isEmpty()) {
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        contest.setTeamPropietario(team.get());

        contestRepository.save(contest);

        salida.setSalida("OK");
        salida.setContest(contest);
        return salida;
    }

    public ContestString updateContest(String contestId, Optional<String> nameContest, Optional<String> teamId, Optional<String> descripcion) {
        ContestString salida = new ContestString();
        //Buscamos el contest
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(contestId));
        if (contest.isEmpty()) {
            salida.setSalida("CONTEST ID DOES NOT EXIST");
            return salida;
        }

        //Si namecontest esta presente lo cambiamos
        if (nameContest.isPresent()) {
            if (contestRepository.existsByNombreContest(nameContest.get())) {
                salida.setSalida("CONTEST NAME DUPLICATED");
                return salida;
            }
            contest.get().setNombreContest(nameContest.get());
        }

        if (teamId.isPresent()) {
            Optional<Team> team = teamRepository.findTeamById(Long.valueOf(teamId.get()));
            if (team.isEmpty()) {
                salida.setSalida("TEAM NOT FOUND");
                return salida;
            }
            contest.get().setTeamPropietario(team.get());
        }
        if (descripcion.isPresent()) {
            contest.get().setDescripcion(descripcion.get());
        }
        contestRepository.save(contest.get());

        salida.setSalida("OK");
        salida.setContest(contest.get());
        return salida;
    }


    public String deleteContest(String idcontest) {
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if (contest.isEmpty()) {
            return "contest NOT FOUND";
        }
        /*
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
         */

        //borramos el contest
        contestRepository.delete(contest.get());

        return "OK";
    }

    public String anyadeProblemaContest(String idContest, String idProblema) {

        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idContest));
        Optional<Problem> problema = problemRepository.findProblemById(Long.valueOf(idProblema));
        if (contest == null) {
            return "contest NOT FOUND";
        }
        if (problema == null) {
            return "USER NOT FOUND";
        }
        if (contest.get().getListaProblemas().contains(problema)) {
            return "PROBLEM DUPLICATED";
        }
        contest.get().addProblem(problema.get());
        contestRepository.save(contest.get());

        return "OK";
    }

    public String deleteProblemFromContest(String idContest, String idProblema) {
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idContest));
        Optional<Problem> problema = problemRepository.findProblemById(Long.valueOf(idProblema));
        if (contest == null) {
            return "contest NOT FOUND";
        }
        if (problema == null) {
            return "USER NOT FOUND";
        }
        if (!contest.get().getListaProblemas().contains(problema)) {
            return "PROBLEM NOT IN CONCURSO";
        }
        contest.get().deleteProblem(problema.get());
        contestRepository.save(contest.get());

        return "OK";
    }

    public String addTeamTocontest(String idTeam, String idcontest) {
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        Optional<Team> team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if (contest == null) {
            return "contest NOT FOUND";
        }
        if (team == null) {
            return "USER NOT FOUND";
        } else {
            if (!contest.get().getListaParticipantes().contains(team)) {
                contest.get().addTeam(team.get());
                contestRepository.save(contest.get());
            } else {
                return "YA ESTA EN EL CONCURSO";
            }
        }
        return "OK";
    }

    public String deleteTeamFromcontest(String idcontest, String idTeam) {
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        Optional<Team> team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if (contest == null) {
            return "contest NOT FOUND";
        }
        if (team == null) {
            return "USER NOT FOUND";
        } else {
            if (!contest.get().getListaParticipantes().contains(team)) {
                return "NO ESTA EN EL CONCURSO";
            } else {
                contest.get().deleteTeam(team.get());
                contestRepository.save(contest.get());
            }
        }
        return "OK";
    }

    public List<ProblemAPI> getProblemsFromConcurso(Contest contest) {
        List<ProblemAPI> problemAPIS = contest.getListaProblemas().stream().map(x -> x.toProblemAPI()).collect(Collectors.toList());
        return problemAPIS;
    }

    public Optional<Contest> getContest(String idContest) {
        return contestRepository.findContestById(Long.valueOf(idContest));
    }

    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }

    public Page<Contest> getContestPage(Pageable pageable) {
        return contestRepository.findAll(pageable);
    }

}
