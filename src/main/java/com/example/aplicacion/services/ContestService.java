package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Language;
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
    @Autowired
    private LanguageService languageService;

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

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(teamId));
        if (teamOptional.isEmpty()) {
            logger.error("Team " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        Team team = teamOptional.get();
        contest.setTeamPropietario(team);

        Language java = languageService.getLanguageByName("java").get();
        contest.addLanguage(java);
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
        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(contestId));
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + contestId + " not found");
            salida.setSalida("CONTEST ID DOES NOT EXIST");
            return salida;
        }
        Contest contest = contestOptional.get();

        //Si namecontest esta presente lo cambiamos
        if (nameContest.isPresent()) {
            if (contestRepository.existsByNombreContest(nameContest.get())) {
                logger.error("Contest name " + nameContest.get() + " duplicated");
                salida.setSalida("CONTEST NAME DUPLICATED");
                return salida;
            }
            String contestName = nameContest.get();
            contest.setNombreContest(contestName);
        }

        if (teamId.isPresent()) {
            Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(teamId.get()));
            if (teamOptional.isEmpty()) {
                logger.error("Team " + teamId.get() + " not found");
                salida.setSalida("TEAM NOT FOUND");
                return salida;
            }
            Team team = teamOptional.get();
            contest.setTeamPropietario(team);
        }
        if (descripcion.isPresent()) {
            contest.setDescripcion(descripcion.get());
        }
        contestRepository.save(contest);

        salida.setSalida("OK");
        salida.setContest(contest);

        logger.debug("Finish update contest " + contestId);
        return salida;
    }


    public String deleteContest(String idcontest) {
        logger.debug("Delete contest " + idcontest);

        Optional<Contest> optionalContest = contestRepository.findContestById(Long.parseLong(idcontest));
        if (optionalContest.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            return "contest NOT FOUND";
        }
        Contest contest = optionalContest.get();

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
        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idContest));
        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(idProblema));

        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idContest + " not found");
            return "contest NOT FOUND";
        }
        Contest contest = contestOptional.get();

        if (problemOptional.isEmpty()) {
            logger.error("Problem " + idProblema + " not found");
            return "PROBLEM NOT FOUND";
        }
        Problem problema = problemOptional.get();

        if (contest.getListaProblemas().contains(problema)) {
            logger.error("Problem " + idProblema + " already in contest");
            return "PROBLEM DUPLICATED";
        }

        contest.addProblem(problema);
        contestRepository.save(contest);

        logger.debug("Finish add problem " + idProblema + " to contest " + idContest);
        return "OK";
    }

    public String deleteProblemFromContest(String idContest, String idProblema) {
        logger.debug("Delete problem " + idProblema + " from contest " + idContest);
        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idContest));
        Optional<Problem> problemaOptional = problemRepository.findProblemById(Long.parseLong(idProblema));

        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idContest + " not found");
            return "contest NOT FOUND";
        }
        Contest contest = contestOptional.get();

        if (problemaOptional.isEmpty()) {
            logger.error("Problem " + idProblema + " not found");
            return "PROBLEM NOT FOUND";
        }
        Problem problema = problemaOptional.get();

        if (!contest.getListaProblemas().contains(problema)) {
            logger.error("Problem " + idProblema + " not in contest " + idContest);
            return "PROBLEM NOT IN CONCURSO";
        }

        contest.deleteProblem(problema);
        contestRepository.save(contest);

        logger.debug("Finish delete problem " + idProblema + " from contest " + idContest);
        return "OK";
    }

    public String addTeamTocontest(String idTeam, String idcontest) {
        logger.debug("Add team/user " + idTeam + " to contest " + idcontest);
        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idcontest));
        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(idTeam));

        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            return "contest NOT FOUND";
        }
        Contest contest = contestOptional.get();

        if (teamOptional.isEmpty()) {
            logger.error("Team/user " + idTeam + " not found");
            return "USER NOT FOUND";
        }

        Team team = teamOptional.get();
        if (!contest.getListaParticipantes().contains(team)) {
            contest.addTeam(team);
            contestRepository.save(contest);
        } else {
            logger.error("Team/user " + idTeam + " already in contest " + idcontest);
            return "YA ESTA EN EL CONCURSO";
        }
        logger.debug("Finish add team/user " + idTeam + " to contest " + idcontest);
        return "OK";
    }

    public String deleteTeamFromcontest(String idcontest, String idTeam) {
        logger.debug("Delete team/user " + idTeam + " from contest " + idcontest);

        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idcontest));
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            return "contest NOT FOUND";
        }
        Contest contest = contestOptional.get();

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(idTeam));
        if (teamOptional.isEmpty()) {
            logger.error("Team/user " + idTeam + " not found");
            return "USER NOT FOUND";
        }
        Team team = teamOptional.get();

        if (!contest.getListaParticipantes().contains(team)) {
            logger.error("Team/user " + idTeam + " not in contest " + idcontest);
            return "NO ESTA EN EL CONCURSO";
        } else {
            contest.deleteTeam(team);
            contestRepository.save(contest);
        }
        logger.debug("Finish delete team/user " + idTeam + " from contest " + idcontest);
        return "OK";
    }

    public List<ProblemAPI> getProblemsFromConcurso(Contest contest) {
        return contest.getListaProblemas().stream().map(x -> x.toProblemAPI()).collect(Collectors.toList());
    }

    public Optional<Contest> getContest(String idContest) {
        return contestRepository.findContestById(Long.parseLong(idContest));
    }

    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }

    public Page<Contest> getContestPage(Pageable pageable) {
        return contestRepository.findAll(pageable);
    }

}
