package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Pojos.SubmissionStringResult;
import com.example.aplicacion.Repository.*;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//This class Sends the proper information to the rabbit queue
@Service
public class SubmissionService {

    Logger logger = LoggerFactory.getLogger(SubmissionService.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private RabbitResultExecutionSender sender;

    public SubmissionStringResult creaYejecutaSubmission(String codigo, String problem, String lenguaje, String fileName, String idContest, String idEquipo) {
        logger.debug("Create and run submission " + fileName + "\nProblem: " + problem + "\nLanguage: " + lenguaje + "\nTeam/user " + idEquipo + "\nContest: " + idContest);
        SubmissionStringResult submissionStringResult;
        //Creamos la submission
        submissionStringResult = creaSubmission(codigo, problem, lenguaje, fileName, idContest, idEquipo);
        if (!submissionStringResult.getSalida().equals("OK")) {
            logger.error("Create and run submission " + fileName + " failed with " + submissionStringResult.getSalida() + "\nProblem: " + problem + "Contest: " + "\nLanguage: " + lenguaje + "\nTeam/user " + idEquipo + "\nContest: " + idContest);
            return submissionStringResult;
        }
        //ejecutamos
        ejecutaSubmission(submissionStringResult.getSubmission());
        logger.debug("Finish create and run submission " + fileName);
        return submissionStringResult;
    }

    public SubmissionStringResult creaSubmission(String codigo, String problem, String lenguaje, String fileName, String idContest, String idEquipo) {
        logger.debug("Create submission " + fileName + "\nProblem: " + problem + "\nLanguage: " + lenguaje + "\nTeam/user " + idEquipo + "\nContest: " + idContest);
        SubmissionStringResult submissionStringResult = new SubmissionStringResult();
        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idContest));
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idContest + " no found");
            submissionStringResult.setSalida("CONTEST NOT FOUND");
            return submissionStringResult;
        }
        Contest contest = contestOptional.get();

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problem));
        if (problemOptional.isEmpty()) {
            logger.error("Problem " + problem + " not found");
            submissionStringResult.setSalida("PROBLEM NOT FOUND");
            return submissionStringResult;
        }
        Problem problema = problemOptional.get();

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(idEquipo));
        if (teamOptional.isEmpty()) {
            logger.error("Team/user " + idEquipo + " not found");
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }
        Team team = teamOptional.get();

        Optional<Language> languageOptional = languageRepository.findLanguageById(Long.parseLong(lenguaje));
        if (languageOptional.isEmpty()) {
            logger.error("Unsupported language " + lenguaje);
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        Language language = languageOptional.get();

        //Comprobamos que el problema pertenezca al contest
        if (!contest.getListaProblemas().contains(problema)) {
            logger.error("Problem " + problem + " not in contest " + idContest);
            submissionStringResult.setSalida("PROBLEM NOT IN CONCURSO");
            return submissionStringResult;
        }

        //Creamos la Submission
        Submission submission = new Submission(codigo, language, fileName);
        //anadimos el probelma a la submsion
        submission.setProblema(problema);
        submission.setContest(contest);
        submission.setTeam(team);

        //Guardamos la entrega
        submissionRepository.save(submission);

        int numeroDeResult = 0;
        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<Sample> datosVisibles = problema.get().getDatosVisibles();
        int numeroDatosVisible = datosVisibles.size();

        for (int i = 0; i < numeroDatosVisible; i++) {
            Result resAux = new Result(datosVisibles.get(i), codigo, language.get(), submission.getFilename(), problema.get().getTimeout(), problema.get().getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        List<Sample> datosOcultos = problema.get().getDatosOcultos();
        int numeroEntradas = datosOcultos.size();

        for (int i = 0; i < numeroEntradas; i++) {
            Result resAux = new Result(datosOcultos.get(i), codigo, language.get(), submission.getFilename(), problema.get().getTimeout(), problema.get().getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        //actualizamos el problema
        problema.addSubmission(submission);
        List<Contest> contestList = team.getListaContestsParticipados();
        if (!contestList.contains(contest)) {
            contestList.add(contest);
        }
        List<Problem> problemList = team.getListaProblemasParticipados();
        if (!problemList.contains(problema)) {
            problemList.add(problema);
        }
        teamRepository.save(team);
        problemRepository.save(problema);

        submissionStringResult.setSalida("OK");
        submissionStringResult.setSubmission(submission);

        logger.debug("Finish create submission " + fileName + "\nSubmission id: " + submission.getId() + "\nLanguage: " + lenguaje + "\nTeam/user " + idEquipo + "\nContest: " + idContest);
        return submissionStringResult;
    }

    //Constructor para ProblemValidator NO PONEMOS EL CONCURSO PARA EVITAR EL BORRADO DE LA SUBMISSION CUANDO SE BORRE EL CONCURSO Y ESE PROBLEMA TMB ESTE EN OTRO CONCURSO
    public SubmissionStringResult creaSubmissionProblemValidator(String codigo, Problem problema, String lenguaje, String fileName, String idContest, String idEquipo) {
        SubmissionStringResult submissionStringResult = new SubmissionStringResult();

        /*
        Contest contest = contestRepository.findContestById(Long.valueOf(idContest));
        if(contest==null){
            submissionStringResult.setSalida("CONCURSO NOT FOUND");
            return submissionStringResult;
        }
         */

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(idEquipo));
        if (teamOptional.isEmpty()) {
            logger.error("Team/user " + idEquipo + " not found");
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }
        Team team = teamOptional.get();

        Optional<Language> languageOptional = languageRepository.findLanguageByNombreLenguaje(lenguaje);
        if (languageOptional.isEmpty()) {
            logger.error("Unsupported language " + lenguaje);
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        Language language = languageOptional.get();

        //Creamos la Submission
        Submission submission = new Submission(codigo, language, fileName);
        //anadimos el probelma a la submsion
        submission.setProblema(problema);
        //submission.setContest(contest);
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
        logger.debug("Create results for submission " + submission.getId() + "\nProblem: " + problema.getId() + "\nLanguage: " + language.getNombreLenguaje());
        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<Sample> datosVisibles = problema.getDatosVisibles();
        int numeroEntradasVisible = datosVisibles.size();
        for (int i = 0; i < numeroEntradasVisible; i++) {
            Result resAux = new Result(datosVisibles.get(i), codigo, language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            submission.addResult(resAux);
        }

        List<Sample> datosOcultos = problema.getDatosOcultos();
        int numeroEntradas = datosOcultos.size();
        for (int i = 0; i < numeroEntradas; i++) {
            Result resAux = new Result(datosOcultos.get(i), codigo, language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            submission.addResult(resAux);
        }
        logger.debug("Finish create results for submission " + submission.getId() + "\nProblem: " + problema.getId() + "\nLanguage: " + language.getNombreLenguaje());
    }

    public void ejecutaSubmission(Submission submission) {
        //Envio de mensaje a la cola
        logger.debug("Send submission " + submission.getId());
        for (Result res : submission.getResults()) {
            sender.sendMessage(res);
        }
        logger.debug("Finish send submission " + submission.getId());
    }

    public Page<Submission> getNSubmissions(int n) {
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);
        return submissionRepository.findAll(firstPageWithTwoElements);
    }

    public String deleteSubmission(String submissionId) {
        logger.debug("Delete submission " + submissionId);
        Optional<Submission> submissionOptional = submissionRepository.findSubmissionById(Long.parseLong(submissionId));
        if (submissionOptional.isEmpty()) {
            logger.error("Submission " + submissionId + " not found");
            return "SUBMISSION NOT FOUND";
        }
        Submission submission = submissionOptional.get();

        //Comprobamos que no se este intentando borrar una SUBMISSIOn pertenciente a un SubmissionProblemValidator
        if (submission.isEsProblemValidator()) {
            logger.error("Submission " + submissionId + " is from problem validator, cannot be deleted from here");
            return "SUBMISSION IS FROM PROBLEM VALIDATOR. YOU MUST DELETE THE PROBLEM TO DELETE THIS SUBMISSION";
        }
        submissionRepository.delete(submission);

        logger.debug("Finish delete submission " + submissionId);
        return "OK";
    }

    public String deleteSubmission(String submissionId, String problemId, String contestId) {
        logger.debug("Delete submission " + submissionId + "\nProblem: " + problemId + "\nContest: " + contestId);

        Optional<Submission> submissionOptional = submissionRepository.findSubmissionById(Long.parseLong(submissionId));
        if (submissionOptional.isEmpty()) {
            logger.error("Submission " + submissionId + " not found\nProblem: " + problemId + "\nContest: " + contestId);
            return "SUBMISSION NOT FOUND";
        }
        Submission submission = submissionOptional.get();

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem " + problemId + " not found");
            return "PROBLEM NOT FOUND";
        }
        Problem problem = problemOptional.get();

        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(contestId));
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + contestId + " not found");
            return "CONCURSO NOT FOUND";
        }
        Contest contest = contestOptional.get();

        if (!contest.getListaProblemas().contains(problem)) {
            logger.error("Problem " + problemId + " not in contest " + contestId);
            return "CONCURSO NOT CONTAINS PROBLEM";
        }
        if (submission.getProblema().equals(problem)) {
            logger.error("Submission " + submissionId + " not in problem " + problemId);
            return "SUBMISSION NO PERTENECE A ESTE PROBLEMA";
        }
        if (submission.getContest().equals(contest)) {
            logger.error("Submission " + submissionId + " not in contest " + contestId);
            return "SUBMISSION NO PERTENCE A ESTE CONCURSO";
        }
        logger.debug("Finish delete submission " + submissionId + "\nProblem: " + problemId + "\nContest: " + contestId);
        return deleteSubmission(submissionId);
    }

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public List<Submission> getSubmissionsFromContest(Contest contest) {
        return contest.getListaSubmissions();
    }

    public List<Submission> getSubmissionFromProblem(Problem problem) {
        return problem.getSubmissions();
    }

    public List<Submission> getSubmissionFromProblemAndContest(Problem problem, Contest contest) {
        return submissionRepository.findSubmissionsByProblemaAndContest(problem, contest);
    }

    public Optional<Submission> getSubmission(String submissionId) {
        return submissionRepository.findSubmissionById(Long.parseLong(submissionId));
    }

    public Page<Submission> getSubmissionsPage(Pageable pageable) {
        return submissionRepository.findAll(pageable);
    }
}
