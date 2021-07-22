package es.urjc.etsii.grafo.iudex.service;

import es.urjc.etsii.grafo.iudex.entity.*;
import es.urjc.etsii.grafo.iudex.pojo.ContestString;
import es.urjc.etsii.grafo.iudex.pojo.ProblemAPI;
import es.urjc.etsii.grafo.iudex.pojo.ProblemScore;
import es.urjc.etsii.grafo.iudex.pojo.TeamScore;
import es.urjc.etsii.grafo.iudex.repository.ContestRepository;
import es.urjc.etsii.grafo.iudex.repository.TeamRepository;
import es.urjc.etsii.grafo.iudex.util.Sanitizer;
import es.urjc.etsii.grafo.iudex.util.TeamScoreComparator;
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
    private static final Logger logger = LoggerFactory.getLogger(ContestService.class);

    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProblemService problemService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private LanguageService languageService;

    public ContestString creaContest(String nameContest, String teamId, Optional<String> description, long startTimestamp, long endTimestamp) {
        logger.debug("Build contest {}", nameContest);
        ContestString salida = new ContestString();
        if (contestRepository.existsContestByNombreContest(nameContest)) {
            logger.error("Contest name duplicated ");
            salida.setSalida("CONTEST NAME DUPLICATED");
            return salida;
        }

        Contest contest = new Contest();
        contest.setNombreContest(nameContest);

        if (description.isPresent()) {
            contest.setDescripcion(description.get());
        }

        Optional<Team> teamOptional = teamService.getTeamFromId(teamId);
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", teamId);
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

        logger.debug("Finish build contest {} with id {}", nameContest, contest.getId());
        return salida;
    }

    public ContestString updateContest(String contestId, Optional<String> nameContest, Optional<String> teamId, Optional<String> descripcion, Optional<Long> startDateTime, Optional<Long> endDateTime) {
        logger.debug("Update contest {}", contestId);
        ContestString salida = new ContestString();

        //Buscamos el contest
        Optional<Contest> contestOptional = getContestById(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", contestId);
            salida.setSalida("CONTEST ID DOES NOT EXIST");
            return salida;
        }
        Contest contest = contestOptional.get();

        //Si namecontest esta presente lo cambiamos
        if (nameContest.isPresent()) {
            String contestName = nameContest.get();
            if (contestRepository.existsContestByNombreContest(contestName)) {
                logger.error("Contest name {} duplicated", contestName);
                salida.setSalida("CONTEST NAME DUPLICATED");
                return salida;
            }
            contest.setNombreContest(contestName);
        }

        if (teamId.isPresent()) {
            Optional<Team> teamOptional = teamService.getTeamFromId(teamId.get());
            if (teamOptional.isEmpty()) {
                logger.error("Team {} not found", teamId.get());
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

        if (endDateTime.isPresent()) {
            long endTimestamp = endDateTime.get();
            contest.setEndDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), TimeZone.getDefault().toZoneId()));
        }

        contestRepository.save(contest);
        salida.setSalida("OK");
        salida.setContest(contest);

        logger.debug("Finish update contest {}", contestId);
        return salida;
    }


    public String deleteContest(String idcontest) {
        logger.debug("Delete contest {}", idcontest);

        Optional<Contest> optionalContest = getContestById(idcontest);
        if (optionalContest.isEmpty()) {
            logger.error("Contest {} not found", idcontest);
            return "contest NOT FOUND";
        }
        Contest contest = optionalContest.get();

        // quitar contest de los participantes
        for (Team teamAux : contest.getListaParticipantes()) {
            teamAux.getListaContestsParticipados().remove(contest);
        }
        //borramos el contest
        contestRepository.delete(contest);

        logger.debug("Finish delete contest {}", idcontest);
        return "OK";
    }

    public String anyadeProblemaContest(String idContest, String idProblema) {
        logger.debug("Add problem {} to contest {}", idProblema, idContest);
        Optional<Contest> contestOptional = getContestById(idContest);
        Optional<Problem> problemOptional = problemService.getProblem(idProblema);

        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", idContest);
            return "contest NOT FOUND";
        }
        Contest contest = contestOptional.get();

        if (problemOptional.isEmpty()) {
            logger.error("Problem {} not found", idProblema);
            return "PROBLEM NOT FOUND";
        }
        Problem problema = problemOptional.get();

        if (contest.getListaProblemas().contains(problema)) {
            logger.error("Problem {} already in contest {}", idProblema, idContest);
            return "PROBLEM ALREADY IN CONTEST";
        }

        contest.addProblem(problema);
        contestRepository.save(contest);

        logger.debug("Finish add problem {} to contest {}", idProblema, idContest);
        return "OK";
    }

    public String deleteProblemFromContest(String idContest, String idProblema) {
        logger.debug("Delete problem {} from contest {}", idProblema, idContest);
        Optional<Contest> contestOptional = getContestById(idContest);
        Optional<Problem> problemaOptional = problemService.getProblem(idProblema);

        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", idContest);
            return "CONTEST NOT FOUND";
        }
        Contest contest = contestOptional.get();

        if (problemaOptional.isEmpty()) {
            logger.error("Problem {} not found", idProblema);
            return "PROBLEM NOT FOUND";
        }
        Problem problema = problemaOptional.get();

        if (!contest.getListaProblemas().contains(problema)) {
            logger.error("Problem {} not in contest {}", idProblema, idContest);
            return "PROBLEM NOT IN CONTEST";
        }

        contest.deleteProblem(problema);
        contestRepository.save(contest);

        logger.debug("Finish delete problem {} from contest {}", idProblema, idContest);
        return "OK";
    }

    public String addTeamToContest(String idcontest, String idTeam) {
        Optional<Contest> contestOptional = getContestById(idcontest);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", idcontest);
            return "CONTEST NOT FOUND";
        }
        Contest contest = contestOptional.get();
        return addTeamToContest(contest, idTeam);
    }

    private String addTeamToContest(Contest contest, String teamId) {
        logger.debug("Add team {} to contest {}", teamId, contest.getId());
        String salida;

        Optional<Team> teamOptional = teamService.getTeamFromId(teamId);
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", teamId);
            return "TEAM NOT FOUND";
        }
        Team team = teamOptional.get();

        if (!contest.getListaParticipantes().contains(team)) {
            team.getListaContestsParticipados().add(contest);
            teamRepository.save(team);
            contest.addTeam(team);
            contestRepository.save(contest);
        } else {
            logger.error("Team {} already in contest {}", teamId, contest.getId());
            return "TEAM ALREADY IN CONTEST";
        }

        logger.debug("Finish add team {} to contest {}", teamId, contest.getId());
        salida = "OK";
        return salida;
    }

    @Transactional
    public String addTeamToContest(String contestId, String[] teamIdList) {
        logger.debug("Adding teams to contest {}", contestId);

        Optional<Contest> contestOptional = getContestById(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", contestId);
            return "CONTEST NOT FOUND!";
        }
        Contest contest = contestOptional.get();

        for (String teamId : teamIdList) {
            teamId = Sanitizer.sanitize(teamId);
            String salida = addTeamToContest(contest, teamId);
            // si hay algun problema se detiene la inserción
            if (!salida.equals("OK")) {
                logger.error("Error while adding team {} to contest {}", teamId, contestId);
                throw new RuntimeException(salida);
            }
        }
        logger.debug("Finish adding teams to contest {}", contestId);
        return "OK";
    }

    public String deleteTeamFromContest(String idContest, String idTeam) {
        Optional<Contest> contestOptional = getContestById(idContest);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found ", idContest);
            return "CONTEST NOT FOUND";
        }
        Contest contest = contestOptional.get();
        return deleteTeamFromContest(contest, idTeam);
    }

    private String deleteTeamFromContest(Contest contest, String teamId) {
        logger.debug("Delete team {} from contest {}", teamId, contest.getId());

        Optional<Team> teamOptional = teamService.getTeamFromId(teamId);
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", teamId);
            return "TEAM NOT FOUND";
        }
        Team team = teamOptional.get();

        if (!contest.getListaParticipantes().contains(team)) {
            logger.error("Team {} not in contest {} ", teamId, contest.getId());
            return "TEAM NOT IN CONTEST";
        } else {
            team.getListaContestsParticipados().remove(contest);
            teamRepository.save(team);
            contest.deleteTeam(team);
            contestRepository.save(contest);
        }
        logger.debug("Finish delete team {} from contest {}", teamId, contest.getId());
        return "OK";
    }

    @Transactional
    public String deleteTeamFromContest(String contestId, String[] teamIdList) {
        logger.debug("Delete some teams from contest {}", contestId);
        Optional<Contest> contestOptional = getContestById(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", contestId);
            return "CONTEST NOT FOUND!";
        }
        Contest contest = contestOptional.get();

        for (String teamId : teamIdList) {
            teamId = Sanitizer.sanitize(teamId);
            String salida = deleteTeamFromContest(contest, teamId);
            if (!salida.equals("OK")) {
                logger.error("Error while removing team {} from contest {}", teamId, contestId);
                throw new RuntimeException(salida);
            }
        }
        logger.debug("Finish delete teams from contest {}", contestId);
        return "OK";
    }

    public List<ProblemAPI> getProblemsFromConcurso(Contest contest) {
        return contest.getListaProblemas().stream().map(x -> x.toProblemAPI()).collect(Collectors.toList());
    }

    public Optional<Contest> getContestById(String idContest) {
        return contestRepository.findContestById(Long.parseLong(idContest));
    }

    public Optional<Contest> getContestByName(String contestName) {
        return contestRepository.findContestByNombreContest(contestName);
    }

    public boolean existsContestByName(String contestName) {
        return contestRepository.existsContestByNombreContest(contestName);
    }

    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }

    public Page<Contest> getContestPage(Pageable pageable) {
        return contestRepository.findAll(pageable);
    }

    public String addLanguageToContest(String contestId, String languageName) {
        Optional<Contest> contestOptional = getContestById(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", contestId);
            return "CONTEST NOT FOUND";
        }
        Contest contest = contestOptional.get();
        return addLanguageToContest(contest, languageName);
    }

    public String removeLanguageFromContest(String contestId, String languageId) {
        logger.debug("Delete language {} from contest {}", languageId, contestId);

        Optional<Contest> contestOptional = getContestById(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", contestId);
            return "CONTEST NOT FOUND";
        }
        Contest contest = contestOptional.get();

        Optional<Language> languageOptional = languageService.getLanguage(languageId);
        if (languageOptional.isEmpty()) {
            logger.error("Language {} not found", languageId);
            return "LANGUAGE NOT FOUND";
        }
        Language language = languageOptional.get();

        if (!contest.getLenguajes().contains(language)) {
            logger.error("Language {} not in contest {}", languageId, contest.getId());
            return "LANGUAGE NOT IN CONTEST";
        }
        contest.removeLanguage(language);
        contestRepository.save(contest);

        logger.debug("Finish delete language {} from contest {}", languageId, contestId);
        return "OK";
    }

    @Transactional
    public String addAcceptedLanguagesToContest(String contestId, String[] languageList) {
        logger.debug("Add accepted languages to contest {}", contestId);

        Optional<Contest> contestOptional = getContestById(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", contestId);
            return "CONTEST NOT FOUND";
        }
        Contest contest = contestOptional.get();

        for (String languageName : languageList) {
            languageName = Sanitizer.sanitize(languageName);
            String salida = addLanguageToContest(contest, languageName);
            if (!salida.equals("OK")) {
                logger.error("Error while adding language {} to contest {}", languageName, contestId);
                throw new RuntimeException(salida);
            }
        }

        logger.debug("Finish add accepted languages to contest {}", contestId);
        return "OK";
    }

    private String addLanguageToContest(Contest contest, String languageName) {
        logger.debug("Adding language {} to contest {}", languageName, contest.getId());
        languageName = languageName.trim().toLowerCase();

        Optional<Language> languageOptional = languageService.getLanguageByName(languageName);
        if (languageOptional.isEmpty()) {
            logger.error("Unknown language {}", languageName);
            return "UNKNOWN LANGUAGE " + languageName.toUpperCase();
        }
        Language language = languageOptional.get();

        // add language if it has not been added
        if (contest.getLenguajes().contains(language)) {
            logger.error("Language {} already in contest {}", languageName, contest.getId());
            return "LANGUAGE ALREADY IN CONTEST";
        }

        contest.addLanguage(language);
        contestRepository.save(contest);

        logger.debug("Finish add language {} to contest {}", languageName, contest.getId());
        return "OK";
    }

    /**
     * Returns the score of the contest in json format given the contestId
     *
     * @param contestId the id of the contest
     * @return the score of the contest in json as String
     */
    public List<TeamScore> getScore(String contestId) {
        logger.debug("Get score of contest {}", contestId);

        Optional<Contest> contestOptional = getContestById(contestId);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", contestId);
            throw new RuntimeException("CONTEST NOT FOUND");
        }
        Contest contest = contestOptional.get();

        logger.debug("Initializing scoreboard");
        Map<Team, TeamScore> teamScoreMap = new HashMap<>();
        for (Team equipo : contest.getListaParticipantes()) {
            TeamScore teamScore = teamScoreMap.getOrDefault(equipo, new TeamScore(equipo.toTeamAPISimple()));
            for (Problem problem : contest.getListaProblemas()) {
                teamScore.addProblemScore(problem, new ProblemScore(problem.toProblemAPISimple()));
            }
            teamScoreMap.put(equipo, teamScore);
        }

        logger.debug("Adding data to scoreboard");
        for (Problem problem : contest.getListaProblemas()) {
            ProblemScore first = null;
            long minExecTime = -1;
            Set<Team> hasFirstAC = new HashSet<>();

            for (Submission entrega : problemService.getSubmissionsFromContestFromProblem(contest, problem)) {
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

                    minExecTime = (minExecTime == -1) ? tiempo : Long.min(minExecTime, tiempo);
                    first = (minExecTime == tiempo || first == null) ? problemScore : first;
                }
                teamScore.addProblemScore(problem, problemScore);
            }

            // actualizar el primer equipo en resolver el problema
            if (first != null) {
                first.setFirst(true);
            }
        }

        logger.debug("Finish create scoreboard");
        // ordenar team score
        List<TeamScore> scores = new ArrayList<>(teamScoreMap.values());
        scores.sort(new TeamScoreComparator());
        return scores;
    }
}
