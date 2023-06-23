package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.*;
import es.urjc.etsii.grafo.iudex.pojos.ContestString;
import es.urjc.etsii.grafo.iudex.pojos.ProblemAPI;
import es.urjc.etsii.grafo.iudex.pojos.ProblemScore;
import es.urjc.etsii.grafo.iudex.pojos.TeamScore;
import es.urjc.etsii.grafo.iudex.repositories.ContestProblemRepository;
import es.urjc.etsii.grafo.iudex.repositories.ContestRepository;
import es.urjc.etsii.grafo.iudex.repositories.ContestTeamRespository;
import es.urjc.etsii.grafo.iudex.repositories.TeamRepository;
import es.urjc.etsii.grafo.iudex.utils.TeamScoreComparator;
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

import static es.urjc.etsii.grafo.iudex.utils.Sanitizer.sanitize;

@Service
public class ContestService {
    private static final Logger logger = LoggerFactory.getLogger(ContestService.class);

    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private ContestProblemRepository contestProblemRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ContestTeamRespository contestTeamRespository;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private UserAndTeamService teamService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private ContestProblemService contestProblemService;

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

        // elminamos contest de los participantes
        contestTeamRespository.deleteByContest(contest);
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

        ContestProblem contestProblem = new ContestProblem(contest,problema,LocalDateTime.now());

        if (contest.getListaProblemas().contains(contestProblem)) {
            logger.error("Problem {} already in contest {}", idProblema, idContest);
            return "PROBLEM ALREADY IN CONTEST";
        }

        contestProblemRepository.save(contestProblem);

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
        Optional<ContestProblem> optionalContestProblem = contestProblemService.getContestProblemByContestAndProblem(contest, problema);

        if (optionalContestProblem.isEmpty() || !contest.getListaProblemas().contains(optionalContestProblem.get())) {
            logger.error("Problem {} not in contest {}", idProblema, idContest);
            return "PROBLEM NOT IN CONTEST";
        }

        ContestProblem contestProblem = optionalContestProblem.get();

        contest.deleteProblem(contestProblem);
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

        ContestTeams contestTeams = new ContestTeams(contest,team,LocalDateTime.now());

        if (!contest.getListaContestsParticipados().contains(contestTeams)) {
            team.getListaContestsParticipados().add(contestTeams);
            teamRepository.save(team);
            contest.addTeam(contestTeams);
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
            teamId = sanitize(teamId);
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

        ContestTeams contestTeams = new ContestTeams(contest,team,LocalDateTime.now());

        if (!contest.getListaContestsParticipados().contains(contestTeams)) {
            logger.error("Team {} not in contest {} ", teamId, contest.getId());
            return "TEAM NOT IN CONTEST";
        } else {
            team.getListaContestsParticipados().remove(contestTeams);
            teamRepository.save(contestTeams.getTeams());
            contest.deleteTeam(contestTeams);
            contestRepository.save(contestTeams.getContest());
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
            teamId = sanitize(teamId);
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
        return contest.getListaProblemas().stream().map(x -> x.getProblem().toProblemAPI()).collect(Collectors.toList());
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

        ContestLanguages contestLanguages = new ContestLanguages(contest,language,LocalDateTime.now());

        if (!contest.getLenguajes().contains(contestLanguages)) {
            logger.error("Language {} not in contest {}", languageId, contest.getId());
            return "LANGUAGE NOT IN CONTEST";
        }

        contest.removeLanguage(contestLanguages);
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
            languageName = sanitize(languageName);
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

        ContestLanguages contestLanguages = new ContestLanguages(contest,language,LocalDateTime.now());

        // add language if it has not been added
        if (contest.getLenguajes().contains(contestLanguages)) {
            logger.error("Language {} already in contest {}", languageName, contest.getId());
            return "LANGUAGE ALREADY IN CONTEST";
        }

        contest.addLanguage(contestLanguages);
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
        for (ContestTeams equipo : contest.getListaContestsParticipados()) {
            TeamScore teamScore = teamScoreMap.getOrDefault(equipo.getTeams(), new TeamScore(equipo.getTeams().toTeamAPISimple()));
            for (ContestProblem problem : contest.getListaProblemas()) {
                teamScore.addProblemScore(problem.getProblem(), new ProblemScore(problem.getProblem().toProblemAPISimple()));
            }
            teamScoreMap.put(equipo.getTeams(), teamScore);
        }

        logger.debug("Adding data to scoreboard");
        for (ContestProblem problem : contest.getListaProblemas()) {
            ProblemScore first = null;
            long minExecTime = -1;
            Set<Team> hasFirstAC = new HashSet<>();

            for (Submission entrega : problemService.getSubmissionsFromContestFromProblem(contest, problem.getProblem())) {
                Team equipo = entrega.getTeam();
                if (hasFirstAC.contains(equipo) || entrega.getResult().toLowerCase().contains("failed in compiler")) {
                    continue; // solo se tienen en cuenta las entregas hasta el primer AC ignorando los fallos de compilación
                }

                TeamScore teamScore = teamScoreMap.get(equipo);
                ProblemScore problemScore = teamScore.getProblemScore(problem.getProblem());

                // actualizar intentos
                problemScore.setTries(problemScore.getTries() + 1);

                // obtener tiempo de las entregas aceptadas
                if (entrega.getResult().equalsIgnoreCase("accepted")) {
                    hasFirstAC.add(equipo);

                    long tiempo = (long) entrega.getExecSubmissionTime();
                    // actualizar puntuacion
                    problemScore.setTimestamp(tiempo);
                    problemScore.evaluate();
                    teamScore.updateScore(problemScore.getScore());

                    minExecTime = (minExecTime == -1) ? tiempo : Long.min(minExecTime, tiempo);
                    first = (minExecTime == tiempo || first == null) ? problemScore : first;
                }
                teamScore.addProblemScore(problem.getProblem(), problemScore);
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
