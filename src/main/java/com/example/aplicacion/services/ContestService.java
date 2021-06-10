package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Pojos.*;
import com.example.aplicacion.Repository.ContestRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
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
    @Autowired
    private TeamService teamService;

    public ContestString creaContest(String nameContest, String teamId, Optional<String> description, long startTimestamp, long endTimestamp) {
        logger.debug("Build contest " + nameContest + "\nTeam: " + teamId + "\nDescription: " + description);
        ContestString salida = new ContestString();
        if (contestRepository.existsByNombreContest(nameContest)) {
            logger.error("Contest name duplicated " + nameContest);
            salida.setSalida("contest NAME DUPLICATED");
            return salida;
        }

        Contest contest = new Contest();
        contest.setNombreContest(nameContest);

        if (description.isPresent()) {
            contest.setDescripcion(description.get());
        }

        Optional<Team> teamOptional = teamService.getTeamFromId(teamId);
        if (teamOptional.isEmpty()) {
            logger.error("Team " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        Team team = teamOptional.get();
        contest.setTeamPropietario(team);

        LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTimestamp), TimeZone.getDefault().toZoneId());
        contest.setStartDateTime(startDateTime);
        LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), TimeZone.getDefault().toZoneId());
        contest.setEndDateTime(endDateTime);
        contestRepository.save(contest);

        salida.setSalida("OK");
        salida.setContest(contest);

        logger.debug("Finish build contest " + nameContest + "\nId: " + contest.getId());
        return salida;
    }

    public ContestString updateContest(String contestId, Optional<String> nameContest, Optional<String> teamId, Optional<String> descripcion, Optional<Long> startDateTime, Optional<Long> endDateTime) {
        logger.debug("Update contest " + contestId);
        ContestString salida = new ContestString();

        //Buscamos el contest
        Optional<Contest> contestOptional = getContest(contestId);
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
            Optional<Team> teamOptional = teamService.getTeamFromId(teamId.get());
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

        if (startDateTime.isPresent()) {
            long startTimestamp = startDateTime.get();
            contest.setStartDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTimestamp), TimeZone.getDefault().toZoneId()));
        }

        if (startDateTime.isPresent()) {
            long endTimestamp = endDateTime.get();
            contest.setEndDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), TimeZone.getDefault().toZoneId()));
        }

        contestRepository.save(contest);

        salida.setSalida("OK");
        salida.setContest(contest);

        logger.debug("Finish update contest " + contestId);
        return salida;
    }


    public String deleteContest(String idcontest) {
        logger.debug("Delete contest " + idcontest);

        Optional<Contest> optionalContest = getContest(idcontest);
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
        Optional<Contest> contestOptional = getContest(idContest);
        Optional<Problem> problemOptional = problemService.getProblem(idProblema);

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
        Optional<Contest> contestOptional = getContest(idContest);
        Optional<Problem> problemaOptional = problemService.getProblem(idProblema);

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

    public String addTeamToContest(String idcontest, String idTeam) {
        Optional<Contest> contestOptional = getContest(idcontest);
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            return "contest NOT FOUND";
        }
        Contest contest = contestOptional.get();
        return addTeamToContest(contest, idTeam);
    }

    private String addTeamToContest(Contest contest, String teamId) {
        logger.debug("Add team/user " + teamId + " to contest " + contest.getId());
        String salida;

        Optional<Team> teamOptional = teamService.getTeamFromId(teamId);
        if (teamOptional.isEmpty()) {
            logger.error("Team/user " + teamId + " not found");
            return "USER NOT FOUND";
        }
        Team team = teamOptional.get();

        if (!contest.getListaParticipantes().contains(team)) {
            team.getListaContestsParticipados().add(contest);
            teamRepository.save(team);
            contest.addTeam(team);
            contestRepository.save(contest);
        } else {
            logger.error("Team/user " + teamId + " already in contest " + contest.getId());
            return "YA ESTA EN EL CONCURSO";
        }

        logger.debug("Finish add team/user " + teamId + " to contest " + contest.getId());
        salida = "OK";
        return salida;
    }

    @Transactional
    public String addTeamToContest(String contestId, String[] teamIdList) {
        logger.debug("Adding teams to contest " + contestId);
        String salida;
        Optional<Contest> contestOptional = getContest(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + contestId + " not found");
            return "CONTEST NOT FOUND!";
        }
        Contest contest = contestOptional.get();

        for (String teamId : teamIdList) {
            salida = addTeamToContest(contest, teamId);

            // si hay algun problema se detiene la inserción
            if (!salida.equals("OK")) {
                logger.error("Error while adding team " + teamId + " to contest " + contestId);
                throw new RuntimeException(salida);
            }
        }
        logger.debug("Finish adding teams to contest " + contestId);
        salida = "OK";
        return salida;
    }

    public String deleteTeamFromContest(String idContest, String idTeam) {
        Optional<Contest> contestOptional = getContest(idContest);
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idContest + " not found");
            return "contest NOT FOUND";
        }
        Contest contest = contestOptional.get();
        return deleteTeamFromContest(contest, idTeam);
    }

    private String deleteTeamFromContest(Contest contest, String teamId) {
        logger.debug("Delete team/user " + teamId + " from contest " + contest.getId());

        Optional<Team> teamOptional = teamService.getTeamFromId(teamId);
        if (teamOptional.isEmpty()) {
            logger.error("Team/user " + teamId + " not found");
            return "USER NOT FOUND";
        }
        Team team = teamOptional.get();

        if (!contest.getListaParticipantes().contains(team)) {
            logger.error("Team/user " + teamId + " not in contest " + contest.getId());
            return "NO ESTA EN EL CONCURSO";
        } else {
            team.getListaContestsParticipados().remove(contest);
            teamRepository.save(team);
            contest.deleteTeam(team);
            contestRepository.save(contest);
        }
        logger.debug("Finish delete team/user " + teamId + " from contest " + contest.getId());
        return "OK";
    }

    @Transactional
    public String deleteTeamFromContest(String contestId, String[] teamIdList) {
        logger.debug("Delete some teams from contest " + contestId);
        String salida;
        Optional<Contest> contestOptional = getContest(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + contestId + " not found");
            return "CONTEST NOT FOUND!";
        }
        Contest contest = contestOptional.get();

        for (String teamId : teamIdList) {
            salida = deleteTeamFromContest(contest, teamId);
            if (!salida.equals("OK")) {
                logger.error("Error while removing team " + teamId + " from contest " + contestId);
                throw new RuntimeException(salida);
            }
        }
        logger.debug("Finish delete teams from contest " + contestId);
        salida = "OK";
        return salida;
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

    public String addLanguageToContest(String contestId, String languageName) {
        Optional<Contest> contestOptional = getContest(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + contestId + " not found!");
            return "CONTEST NOT FOUND!";
        }
        Contest contest = contestOptional.get();
        return addLanguageToContest(contest, languageName);
    }

    public String removeLanguageFromContest(String contestId, String languageId) {
        logger.debug("Delete language " + languageId + " from contest " + contestId);
        String salida;

        Optional<Contest> contestOptional = getContest(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + contestId + " not found!");
            salida = "CONTEST NOT FOUND!";
            return salida;
        }
        Contest contest = contestOptional.get();

        Optional<Language> languageOptional = languageService.getLanguage(languageId);
        if (languageOptional.isEmpty()) {
            logger.error("Language " + languageId + " not found!");
            salida = "LANGUAGE NOT FOUND!";
            return salida;
        }
        Language language = languageOptional.get();

        if (!contest.getLenguajes().contains(language)) {
            logger.error("Language " + languageId + " not in contest " + contest.getId());
            salida = "LANGUAGE NOT IN CONTEST!";
            return salida;
        }
        contest.removeLanguage(language);
        contestRepository.save(contest);

        logger.debug("Finish delete language " + languageId + " from contest " + contestId);
        salida = "OK";
        return salida;
    }

    @Transactional
    public String addAcceptedLanguagesToContest(String contestId, String[] languageList) {
        logger.debug("Add accepted languages to contest " + contestId);
        String salida;

        Optional<Contest> contestOptional = getContest(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + contestId + " not found!");
            salida = "CONTEST NOT FOUND!";
            return salida;
        }
        Contest contest = contestOptional.get();

        for (String languageName : languageList) {
            salida = addLanguageToContest(contest, languageName);
            // si hay algún problema se detiene la operación
            if (!salida.equals("OK")) {
                logger.error("Error while adding language " + languageName + " to contest " + contestId);
                throw new RuntimeException(salida);
            }
        }

        logger.debug("Finish add accepted languages to contest " + contestId);
        salida = "OK";
        return salida;
    }

    private String addLanguageToContest(Contest contest, String languageName) {
        logger.debug("Adding language " + languageName + " to contest " + contest.getId());
        String salida;
        languageName = languageName.trim().toLowerCase();

        Optional<Language> languageOptional = languageService.getLanguageByName(languageName);
        if (languageOptional.isEmpty()) {
            logger.error("Unknown language " + languageName);
            salida = "UNKNOWN LANGUAGE " + languageName.toUpperCase() + "!";
            return salida;
        }
        Language language = languageOptional.get();

        // add language if it has not been added
        if (contest.getLenguajes().contains(language)) {
            logger.error("Language " + languageName + " already in contest " + contest.getId());
            salida = "LANGUAGE ALREADY IN CONTEST!";
            return salida;
        }

        contest.addLanguage(language);
        contestRepository.save(contest);

        logger.debug("Finish add language " + languageName + " to contest " + contest.getId());
        salida = "OK";
        return salida;
    }

    /**
     * Returns the score of the contest in json format given the contestId
     *
     * @param contestId the id of the contest
     * @return the score of the contest in json as String
     */
    public List<TeamScore> getScore(String contestId) {
        logger.debug("Get score received for contest " + contestId);
        Optional<Contest> contestOptional = getContest(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + contestId + " not found!");
            throw new RuntimeException("CONTEST NOT FOUND!");
        }
        Contest contest = contestOptional.get();

        logger.debug("Initializing scoreboard");
        Map<Team, TeamScore> teamScoreMap = new HashMap<>();
        for (Team equipo : contest.getListaParticipantes()) {
            TeamScore teamScore = teamScoreMap.getOrDefault(equipo, new TeamScore(equipo.toTeamAPISimple()));
            for (Problem problem : contest.getListaProblemas()) {
                teamScore.addProblemScore(new ProblemScore(problem.toProblemAPISimple()), problem);
            }
            teamScoreMap.put(equipo, teamScore);
        }

        logger.debug("Add data to scoreboard");
        for (Problem problem : contest.getListaProblemas()) {
            ProblemScore first = null;
            long min_exec_time = -1;
            Set<Team> hasFirstAC = new HashSet<>();

            for (Submission entrega : problemService.getSubmissionsFromContestFromProblem(contest, problem)) {
                //if (entrega.isEsProblemValidator()) continue; // saltar entregas de validación del problema
                Team equipo = entrega.getTeam();
                if (hasFirstAC.contains(equipo) || entrega.getResultado().toLowerCase().contains("failed in compiler")) {
                    continue; // solo se tienen en cuenta las entregas hasta el primer AC ignorando los fallos de compilación
                }

                TeamScore teamScore = teamScoreMap.get(equipo);
                ProblemScore problemScore = teamScore.getProblemScore(problem);

                // actualizar intentos
                problemScore.setTries(problemScore.getTries() + 1);

                // obtener tiempo de las entregas aceptadas
                if (entrega.getResultado().equalsIgnoreCase("accepted")) {
                    hasFirstAC.add(equipo);

                    long tiempo = (long) entrega.getExecSubmissionTime();
                    // actualizar puntuacion
                    problemScore.setTimestamp(tiempo);
                    problemScore.evaluate();
                    teamScore.updateScore(problemScore.getScore());

                    min_exec_time = (min_exec_time == -1) ? tiempo : Long.min(min_exec_time, tiempo);
                    first = (min_exec_time == tiempo || first == null) ? problemScore : first;
                }
                teamScore.addProblemScore(problemScore, problem);
            }

            // actualizar el primer equipo en resolver el problema
            if (first != null) {
                first.setFirst(true);
            }
        }

        logger.debug("Finish create scoreboard");
        // ordenar team score
        List<TeamScore> scores = new ArrayList<>(teamScoreMap.values());
        Collections.sort(scores, new TeamScoreComparator());
        return scores;
    }
}
