package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.pojos.SubmissionStringResult;
import es.urjc.etsii.grafo.iudex.rabbitmq.RabbitResultExecutionSender;
import es.urjc.etsii.grafo.iudex.entities.*;
import es.urjc.etsii.grafo.iudex.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//This class Sends the proper information to the rabbit queue
@Service
public class SubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitResultExecutionSender sender;

    public SubmissionStringResult creaYejecutaSubmission(String codigo, String problem, String lenguaje, String fileName, String idContest, String idEquipo) {
        logger.debug("Create and run submission {} for problem {} of contest {}", fileName, problem, idContest);
        SubmissionStringResult submissionStringResult;
        //Creamos la submission
        submissionStringResult = creaSubmission(codigo, problem, lenguaje, fileName, idContest, idEquipo);
        if (!submissionStringResult.getSalida().equals("OK")) {
            logger.error("Create and run submission {} failed with {}", fileName, submissionStringResult.getSalida());
            return submissionStringResult;
        }
        //ejecutamos
        ejecutaSubmission(submissionStringResult.getSubmission());
        logger.debug("Finish create and run submission {} for problem {} of contest {}", fileName, problem, idContest);
        return submissionStringResult;
    }

    public SubmissionStringResult creaSubmission(String codigo, String problem, String lenguaje, String fileName, String idContest, String idEquipo) {
        logger.debug("Create submission {} for problem {} of contest {}", fileName, problem, idContest);
        SubmissionStringResult submissionStringResult = new SubmissionStringResult();

        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idContest));
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", idContest);
            submissionStringResult.setSalida("CONTEST NOT FOUND");
            return submissionStringResult;
        }
        Contest contest = contestOptional.get();

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problem));
        if (problemOptional.isEmpty()) {
            logger.error("Problem {} not found", problem);
            submissionStringResult.setSalida("PROBLEM NOT FOUND");
            return submissionStringResult;
        }
        Problem problema = problemOptional.get();

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(idEquipo));
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", idEquipo);
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }
        Team team = teamOptional.get();

        Optional<Language> languageOptional = languageRepository.findLanguageById(Long.parseLong(lenguaje));
        if (languageOptional.isEmpty()) {
            logger.error("Unsupported language {}", lenguaje);
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        Language language = languageOptional.get();

       ContestProblem contestProblem = new ContestProblem(contest,problema,LocalDateTime.now());

        //Comprobamos que el problema pertenezca al contest
        if (!contest.getListaProblemas().contains(contestProblem)) {
            logger.error("Problem {} not in contest {}", problem, idContest);
            submissionStringResult.setSalida("PROBLEM NOT IN CONTEST");
            return submissionStringResult;
        }

        // comprobar si el problema tiene casos de prueba
        if (!problema.hasTestCaseFiles()) {
            logger.error("Problem {} does not contain any test case files", problem);
            submissionStringResult.setSalida("PROBLEM IS EMPTY");
            return submissionStringResult;
        }

        //Creamos la Submission
        Submission submission = new Submission(codigo, language, fileName);
        //anadimos el probelma a la submsion
        submission.setProblem(problema);
        submission.setContest(contest);
        submission.setTeam(team);

        //Guardamos la entrega
        submissionRepository.save(submission);

        int numeroDeResult = 0;

        //Creamos los result que tienen que ir con la submission y anadimos a submision
        for (Sample datos : problema.getData()) {
            Result resAux = new Result(datos, codigo, language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        TeamsProblems teamsProblems = new TeamsProblems(team,problema, LocalDateTime.now());

        ContestTeams contestTeams = new ContestTeams(contest,team, LocalDateTime.now());

        //actualizamos el problema
        problema.addSubmission(submission);
        team.getListaContestsParticipados().add(contestTeams);
        team.getListaProblemasParticipados().add(teamsProblems);

        teamRepository.save(team);
        problemRepository.save(problema);

        submissionStringResult.setSalida("OK");
        submissionStringResult.setSubmission(submission);

        logger.debug("Finish create submission {} for problem {} of contest {}", fileName, problema.getId(), contest.getId());
        return submissionStringResult;
    }

    //para ProblemValidator NO PONEMOS EL CONCURSO PARA EVITAR EL BORRADO DE LA SUBMISSION CUANDO SE BORRE EL CONCURSO Y ESE PROBLEMA TMB ESTE EN OTRO CONCURSO
    public SubmissionStringResult creaSubmissionProblemValidator(String codigo, Problem problema, String lenguaje, String fileName, String idEquipo) {
        SubmissionStringResult submissionStringResult = new SubmissionStringResult();

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(idEquipo));
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", idEquipo);
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }
        Team team = teamOptional.get();

        Optional<Language> languageOptional = languageRepository.findLanguageByNombreLenguaje(lenguaje);
        if (languageOptional.isEmpty()) {
            logger.error("Unsupported language {}", lenguaje);
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        Language language = languageOptional.get();

        //Creamos la Submission
        Submission submission = new Submission(codigo, language, fileName);
        //anadimos el probelma a la submsion
        submission.setProblem(problema);
        submission.setTeam(team);
        submission.setEsProblemValidator(true);
        //Guardamos la submission
        problema.addSubmission(submission);

        submissionStringResult.setSalida("OK");
        submissionStringResult.setSubmission(submission);
        return submissionStringResult;
    }

    public void creaResults(Submission submission, Problem problema, String codigo, Language language) {
        int numeroDeResult = 0;
        logger.debug("Create results for submission {} of problem {}", submission.getId(), problema.getId());
        //Creamos los result que tienen que ir con la submission y anadimos a submision
        for (Sample sample : problema.getData()) {
            Result resAux = new Result(sample, codigo, language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            submission.addResult(resAux);
        }
        logger.debug("Finish create results for submission {} of problem {}", submission.getId(), problema.getId());
    }

    public void ejecutaSubmission(Submission submission) {
        //Envio de mensaje a la cola
        logger.debug("Send submission {}", submission.getId());
        for (Result res : submission.getResults()) {
            sender.sendMessage(res);
        }
        logger.debug("Finish send submission {}", submission.getId());
    }

    public Page<Submission> getNSubmissions(int n) {
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);
        return submissionRepository.findAll(firstPageWithTwoElements);
    }

    public String deleteSubmission(String submissionId) {
        logger.debug("Delete submission {}", submissionId);
        Optional<Submission> submissionOptional = submissionRepository.findSubmissionById(Long.parseLong(submissionId));
        if (submissionOptional.isEmpty()) {
            logger.error("Submission {} not found", submissionId);
            return "SUBMISSION NOT FOUND";
        }
        Submission submission = submissionOptional.get();

        //Comprobamos que no se este intentando borrar una SUBMISSIOn pertenciente a un SubmissionProblemValidator
        if (submission.isEsProblemValidator()) {
            logger.error("Submission {} is from problem validator, cannot be deleted from here", submissionId);
            return "SUBMISSION IS FROM PROBLEM VALIDATOR. YOU MUST DELETE THE PROBLEM TO DELETE THIS SUBMISSION";
        }
        submissionRepository.delete(submission);

        logger.debug("Finish delete submission {}", submissionId);
        return "OK";
    }

    public String deleteSubmission(String submissionId, String problemId, String contestId) {
        logger.debug("Delete submission {} from problem {} of contest {}", submissionId, problemId, contestId);

        Optional<Submission> submissionOptional = submissionRepository.findSubmissionById(Long.parseLong(submissionId));
        if (submissionOptional.isEmpty()) {
            logger.error("Submission {} not found", submissionId);
            return "SUBMISSION NOT FOUND";
        }
        Submission submission = submissionOptional.get();

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem {} not found", problemId);
            return "PROBLEM NOT FOUND";
        }
        Problem problem = problemOptional.get();

        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(contestId));
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", contestId);
            return "CONTEST NOT FOUND";
        }
        Contest contest = contestOptional.get();

        ContestProblem contestProblem = new ContestProblem(contest,problem,LocalDateTime.now());

        if (!contest.getListaProblemas().contains(contestProblem)) {
            logger.error("Problem {} not in contest {}", problemId, contestId);
            return "PROBLEM NOT IN CONTEST";
        }
        if (submission.getProblem().equals(problem)) {
            logger.error("Submission {} not in problem {}", submissionId, problemId);
            return "SUBMISSION NOT IN PROBLEM";
        }
        if (submission.getContest().equals(contest)) {
            logger.error("Submission {} not in contest {}", submissionId, contestId);
            return "SUBMISSION NOT IN CONTEST";
        }
        logger.debug("Finish delete submission {} from problem {} of contest {}", submissionId, problemId, contestId);
        return deleteSubmission(submissionId);
    }

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public Set<Submission> getSubmissionsFromContest(Contest contest) {
        return contest.getListaSubmissions();
    }

    public Set<Submission> getSubmissionFromProblem(Problem problem) {
        return problem.getSubmissions();
    }

    public List<Submission> getSubmissionFromProblemAndContest(Problem problem, Contest contest) {
        return submissionRepository.findSubmissionsByProblemAndContest(problem, contest);
    }

    public Optional<Submission> getSubmission(String submissionId) {
        return submissionRepository.findSubmissionById(Long.parseLong(submissionId));
    }

    public Page<Submission> getSubmissionsPage(Pageable pageable) {
        return submissionRepository.findAll(pageable);
    }
}
