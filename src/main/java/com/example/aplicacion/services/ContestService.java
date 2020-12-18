package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Pojos.ContestString;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Repository.ContestRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContestService {
    Logger logger = LoggerFactory.getLogger(ContestService.class);
    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ProblemService problemService;

    public ContestString creaContest(String nameContest, String teamId, Optional<String> descripcion) {
        logger.info("Build contest " + nameContest + "\nTeam: " + teamId + "\nDescription: " + descripcion);
        ContestString salida = new ContestString();
        if (contestRepository.existsByNombreContest(nameContest)) {
            logger.error("Contest name duplicated " + nameContest);
            salida.setSalida("contest NAME DUPLICATED");
            return salida;
        }

        Contest contest = new Contest();
        contest.setNombreContest(nameContest);

        if (descripcion.isPresent()) {
            contest.setDescripcion(descripcion.get());
        }
        Team team = teamRepository.findTeamById(Long.valueOf(teamId));
        if (team == null) {
            logger.error("Team " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        contest.setTeamPropietario(team);

        contestRepository.save(contest);

        salida.setSalida("OK");
        salida.setContest(contest);
        logger.info("Finish build contest " + nameContest + "\nId: " + contest.getId());
        return salida;
    }

    public ContestString updateContest(String contestId, Optional<String> nameContest, Optional<String> teamId, Optional<String> descripcion) {
        logger.info("Update contest " + contestId);
        ContestString salida = new ContestString();
        //Buscamos el contest
        Contest contest = contestRepository.findContestById(Long.valueOf(contestId));
        if (contest == null) {
            logger.error("Contest " + contestId + " with name " + nameContest + " doesn't exist");
            salida.setSalida("CONTEST ID DOES NOT EXIST");
            return salida;
        }

        //Si namecontest esta presente lo cambiamos
        if (nameContest.isPresent()) {
            if (contestRepository.existsByNombreContest(nameContest.get())) {
                logger.error("Contest name " + nameContest.get() + " duplicated");
                salida.setSalida("CONTEST NAME DUPLICATED");
                return salida;
            }
            contest.setNombreContest(nameContest.get());
        }

        if (teamId.isPresent()) {
            Team team = teamRepository.findTeamById(Long.valueOf(teamId.get()));
            if (team == null) {
                logger.error("Team " + teamId.get() + " not found");
                salida.setSalida("TEAM NOT FOUND");
                return salida;
            }
            contest.setTeamPropietario(team);
        }
        if (descripcion.isPresent()) {
            contest.setDescripcion(descripcion.get());
        }
        contestRepository.save(contest);

        salida.setSalida("OK");
        salida.setContest(contest);
        logger.info("Finish update contest " + contestId);
        return salida;
    }


    public String deleteContest(String idcontest) {
        logger.info("Delete contest " + idcontest);
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if (contest == null) {
            logger.error("Contest " + idcontest + " not found");
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
        contestRepository.delete(contest);
        logger.info("Finish delete contest " + idcontest);
        return "OK";
    }

    public String anyadeProblemaContest(String idContest, String idProblema) {
        logger.info("Add problem " + idProblema + " to contest " + idContest);
        Contest contest = contestRepository.findContestById(Long.valueOf(idContest));
        Problem problema = problemRepository.findProblemById(Long.valueOf(idProblema));
        if (contest == null) {
            logger.error("Contest " + idContest + " not found");
            return "contest NOT FOUND";
        }
        if (problema == null) {
            logger.error("Problem " + idProblema + " not found");
            return "PROBLEM NOT FOUND";
        }
        if (contest.getListaProblemas().contains(problema)) {
            logger.error("Problem " + idProblema + " already in contest");
            return "PROBLEM DUPLICATED";
        }
        contest.addProblem(problema);
        contestRepository.save(contest);
        logger.info("Finish add problem " + idProblema + " to contest " + idContest);
        return "OK";
    }

    public String deleteProblemFromContest(String idContest, String idProblema) {
        logger.info("Delete problem " + idProblema + " from contest " + idContest);
        Contest contest = contestRepository.findContestById(Long.valueOf(idContest));
        Problem problema = problemRepository.findProblemById(Long.valueOf(idProblema));
        if (contest == null) {
            logger.error("Contest " + idContest + " not found");
            return "contest NOT FOUND";
        }
        if (problema == null) {
            logger.error("Problem " + idProblema + " not found");
            return "PROBLEM NOT FOUND";
        }
        if (!contest.getListaProblemas().contains(problema)) {
            logger.error("Problem " + idProblema + " not in contest " + idContest);
            return "PROBLEM NOT IN CONCURSO";
        }
        contest.deleteProblem(problema);
        contestRepository.save(contest);

        logger.info("Finish delete problem " + idProblema + " from contest " + idContest);
        return "OK";
    }

    public String addTeamTocontest(String idTeam, String idcontest) {
        logger.info("Add team/user " + idTeam + " to contest " + idcontest);
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        Team team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if (contest == null) {
            logger.error("Contest " + idcontest + " not found");
            return "contest NOT FOUND";
        }
        if (team == null) {
            logger.error("Team/user " + idTeam + " not found");
            return "USER NOT FOUND";
        } else {
            if (!contest.getListaParticipantes().contains(team)) {
                contest.addTeam(team);
                contestRepository.save(contest);
            } else {
                logger.error("Team/user " + idTeam + " already in contest " + idcontest);
                return "YA ESTA EN EL CONCURSO";
            }
        }
        logger.info("Finish add team/user " + idTeam + " to contest " + idcontest);
        return "OK";
    }


    public String deleteTeamFromcontest(String idcontest, String idTeam) {
        logger.info("Delete team/user " + idTeam + " from contest " + idcontest);
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        Team team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if (contest == null) {
            logger.error("Contest " + idcontest + " not found");
            return "contest NOT FOUND";
        }
        if (team == null) {
            logger.error("Team/user " + idTeam + " not found");
            return "USER NOT FOUND";
        } else {
            if (!contest.getListaParticipantes().contains(team)) {
                logger.error("Team/user " + idTeam + " not in contest " + idcontest);
                return "NO ESTA EN EL CONCURSO";
            } else {
                contest.deleteTeam(team);
                contestRepository.save(contest);
            }
        }
        logger.info("Finish delete team/user " + idTeam + " from contest " + idcontest);
        return "OK";
    }

    public List<ProblemAPI> getProblemsFromConcurso(Contest contest) {
        List<ProblemAPI> problemAPIS = contest.getListaProblemas().stream().map(x -> x.toProblemAPI()).collect(Collectors.toList());
        return problemAPIS;
    }

    public Contest getContest(String idContest) {
        return contestRepository.findContestById(Long.valueOf(idContest));
    }

    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }

    public Page<Contest> getContestPage(Pageable pageable) {
        return contestRepository.findAll(pageable);
    }

}
