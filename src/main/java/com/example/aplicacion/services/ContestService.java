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
        logger.debug("Build contest " + nameContest + "\nTeam: " + teamId + "\nDescription: " + descripcion);
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

        Optional<Team> team = teamRepository.findTeamById(Long.valueOf(teamId));
        if (team.isEmpty()) {
            logger.error("Team " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        contest.setTeamPropietario(team.get());

        contestRepository.save(contest);

        salida.setSalida("OK");
        salida.setContest(contest);

        logger.debug("Finish build contest " + nameContest + "\nId: " + contest.getId());
        return salida;
    }

    public ContestString updateContest(String contestId, Optional<String> nameContest, Optional<String> teamId, Optional<String> descripcion) {
        logger.debug("Update contest " + contestId);
        ContestString salida = new ContestString();

        //Buscamos el contest
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(contestId));
        if (contest.isEmpty()) {
            logger.error("Contest " + contestId + " not found");
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
            contest.get().setNombreContest(nameContest.get());
        }

        if (teamId.isPresent()) {
            Optional<Team> team = teamRepository.findTeamById(Long.valueOf(teamId.get()));
            if (team.isEmpty()) {
                logger.error("Team " + teamId.get() + " not found");
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

        logger.debug("Finish update contest " + contestId);
        return salida;
    }


    public String deleteContest(String idcontest) {
        logger.debug("Delete contest " + idcontest);
        Optional<Contest> optional = contestRepository.findContestById(Long.valueOf(idcontest));
        if (optional.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            return "contest NOT FOUND";
        }
        Contest contest = optional.get();
        // quitar contest de los participantes
        for (Team teamAux : contest.getListaParticipantes()) {
            teamAux.getListaContestsParticipados().remove(contest);
        }

        //borramos el contest
        contestRepository.delete(contest);

        logger.debug("Finish delete contest " + idcontest);
        return "OK";
    }

    public String anyadeProblemaContest(String idContest, String idProblema) {
        logger.debug("Add problem " + idProblema + " to contest " + idContest);
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idContest));
        Optional<Problem> problema = problemRepository.findProblemById(Long.valueOf(idProblema));

        if (contest.isEmpty()) {
            logger.error("Contest " + idContest + " not found");
            return "contest NOT FOUND";
        }
        if (problema.isEmpty()) {
            logger.error("Problem " + idProblema + " not found");
            return "PROBLEM NOT FOUND";
        }
        if (contest.get().getListaProblemas().contains(problema.get())) {
            logger.error("Problem " + idProblema + " already in contest");
            return "PROBLEM DUPLICATED";
        }

        contest.get().addProblem(problema.get());
        contestRepository.save(contest.get());

        logger.debug("Finish add problem " + idProblema + " to contest " + idContest);
        return "OK";
    }

    public String deleteProblemFromContest(String idContest, String idProblema) {
        logger.debug("Delete problem " + idProblema + " from contest " + idContest);
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idContest));
        Optional<Problem> problema = problemRepository.findProblemById(Long.valueOf(idProblema));

        if (contest.isEmpty()) {
            logger.error("Contest " + idContest + " not found");
            return "contest NOT FOUND";
        }
        if (problema.isEmpty()) {
            logger.error("Problem " + idProblema + " not found");
            return "PROBLEM NOT FOUND";
        }
        if (!contest.get().getListaProblemas().contains(problema.get())) {
            logger.error("Problem " + idProblema + " not in contest " + idContest);
            return "PROBLEM NOT IN CONCURSO";
        }

        contest.get().deleteProblem(problema.get());
        contestRepository.save(contest.get());

        logger.debug("Finish delete problem " + idProblema + " from contest " + idContest);
        return "OK";
    }

    public String addTeamTocontest(String idTeam, String idcontest) {
        logger.debug("Add team/user " + idTeam + " to contest " + idcontest);
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        Optional<Team> team = teamRepository.findTeamById(Long.valueOf(idTeam));

        if (contest.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            return "contest NOT FOUND";
        }
        if (team.isEmpty()) {
            logger.error("Team/user " + idTeam + " not found");
            return "USER NOT FOUND";
        } else {
            if (!contest.get().getListaParticipantes().contains(team.get())) {
                contest.get().addTeam(team.get());
                contestRepository.save(contest.get());
            } else {
                logger.error("Team/user " + idTeam + " already in contest " + idcontest);
                return "YA ESTA EN EL CONCURSO";
            }
        }

        logger.debug("Finish add team/user " + idTeam + " to contest " + idcontest);
        return "OK";
    }

    public String deleteTeamFromcontest(String idcontest, String idTeam) {
        logger.debug("Delete team/user " + idTeam + " from contest " + idcontest);
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        Optional<Team> team = teamRepository.findTeamById(Long.valueOf(idTeam));
        if (contest.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            return "contest NOT FOUND";
        }
        if (team.isEmpty()) {
            logger.error("Team/user " + idTeam + " not found");
            return "USER NOT FOUND";
        } else {
            if (!contest.get().getListaParticipantes().contains(team.get())) {
                logger.error("Team/user " + idTeam + " not in contest " + idcontest);
                return "NO ESTA EN EL CONCURSO";
            } else {
                contest.get().deleteTeam(team.get());
                contestRepository.save(contest.get());
            }
        }
        logger.debug("Finish delete team/user " + idTeam + " from contest " + idcontest);
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
