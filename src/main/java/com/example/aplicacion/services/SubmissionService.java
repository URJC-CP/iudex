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

//This class Sends the propeer information to the rabbitqueu
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
        SubmissionStringResult submissionStringResult = new SubmissionStringResult();
        logger.info("Create submission " + fileName + "\nProblem: " + problem + "\nLanguage: " + lenguaje + "\nTeam/user " + idEquipo + "\nContest: " + idContest);
        Contest contest = contestRepository.findContestById(Long.valueOf(idContest));
        if (contest == null) {
            logger.error("Contest " + idContest + " no found");
            submissionStringResult.setSalida("CONCURSO NOT FOUND");
            return submissionStringResult;
        }

        Problem problema = problemRepository.findProblemById(Long.valueOf(problem));
        if (problema == null) {
            logger.error("Problem " + problem + " not found");
            submissionStringResult.setSalida("PROBLEM NOT FOUND");
            return submissionStringResult;
        }
        Team team = teamRepository.findTeamById(Long.valueOf(idEquipo));
        if (team == null) {
            logger.error("Team/user " + idEquipo + " not found");
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }

        Language language = languageRepository.findLanguageById(Long.valueOf(lenguaje));
        if (language == null) {
            logger.error("Unsupported language " + language.getNombreLenguaje());
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        //Creamos la Submission
        Submission submission = new Submission(codigo, language, fileName);
        //anadimos el probelma a la submsion
        submission.setProblema(problema);
        submission.setContest(contest);
        submission.setTeam(team);

        //Para que le asigne el@Id
        submissionRepository.save(submission);
        //Comprobamos q el problema pertenezca al contest
        if (!contest.getListaProblemas().contains(problema)) {
            logger.error("Problem " + problem + " not in contest " + idContest);
            submissionStringResult.setSalida("PROBLEM NOT IN CONCURSO");
            return submissionStringResult;
        }

        int numeroDeResult = 0;
        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<InNOut> entradasProblemaVisible = problema.getEntradaVisible();
        List<InNOut> salidaCorrectaProblemaVisible = problema.getSalidaVisible();
        int numeroEntradasVisible = entradasProblemaVisible.size();
        for (int i = 0; i < numeroEntradasVisible; i++) {
            Result resAux = new Result(entradasProblemaVisible.get(i), codigo, salidaCorrectaProblemaVisible.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        List<InNOut> entradasProblema = problema.getEntradaOculta();
        List<InNOut> salidaCorrectaProblema = problema.getSalidaOculta();
        int numeroEntradas = entradasProblema.size();
        for (int i = 0; i < numeroEntradas; i++) {
            Result resAux = new Result(entradasProblema.get(i), codigo, salidaCorrectaProblema.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        //Guardamos la submission
        problema.addSubmission(submission);
        problemRepository.save(problema);

        submissionStringResult.setSalida("OK");
        submissionStringResult.setSubmission(submission);

        logger.info("Finish create submission " + fileName + "\nSubmission id: " + submission.getId() + "\nLanguage: " + lenguaje + "\nTeam/user " + idEquipo + "\nContest: " + idContest);
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

        Team team = teamRepository.findTeamById(Long.valueOf(idEquipo));
        if (team == null) {
            logger.error("Team/user " + idEquipo + " not found");
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }

        Language language = languageRepository.findLanguageByNombreLenguaje(lenguaje);
        if (language == null) {
            logger.error("Unsupported language " + lenguaje);
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        //Creamos la Submission
        Submission submission = new Submission(codigo, language, fileName);
        //Para que le asigne el@Id
        //SEBORRATEMPORALMENTEsubmissionRepository.save(submission);

        //anadimos el probelma a la submsion
        submission.setProblema(problema);
        //submission.setContest(contest);
        submission.setTeam(team);

        int numeroDeResult = 0;

        //Lo movemos de aqui por problemas y ejecuta por separado
        /*
        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<InNOut> entradasProblemaVisible = problema.getEntradaVisible();
        List<InNOut> salidaCorrectaProblemaVisible = problema.getSalidaVisible();
        int numeroEntradasVisible = entradasProblemaVisible.size();
        for(int i =0; i<numeroEntradasVisible; i++){
            Result resAux = new Result(entradasProblemaVisible.get(i), codigo, salidaCorrectaProblemaVisible.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit() );
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            //resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        List<InNOut> entradasProblema = problema.getEntradaOculta();
        List<InNOut> salidaCorrectaProblema = problema.getSalidaOculta();
        int numeroEntradas = entradasProblema.size();
        for(int i =0; i<numeroEntradas; i++){
            Result resAux = new Result(entradasProblema.get(i), codigo, salidaCorrectaProblema.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            //resultRepository.save(resAux);
            submission.addResult(resAux);
        }
         */

        submission.setEsProblemValidator(true);
        //Guardamos la submission

        problema.addSubmission(submission);
        //SEBORRATEMPORALMENTEproblemRepository.save(problema);
        //SE BORRA TEMPORALMENTE

        submissionStringResult.setSalida("OK");
        submissionStringResult.setSubmission(submission);
        return submissionStringResult;
    }

    public void creaResults(Submission submission, Problem problema, String codigo, Language language) {
        int numeroDeResult = 0;
        logger.info("Create results for submission " + submission.getId() + "\nProblem: " + problema.getId() + "\nLanguage: " + language.getNombreLenguaje());
        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<InNOut> entradasProblemaVisible = problema.getEntradaVisible();
        List<InNOut> salidaCorrectaProblemaVisible = problema.getSalidaVisible();
        int numeroEntradasVisible = entradasProblemaVisible.size();
        for (int i = 0; i < numeroEntradasVisible; i++) {
            Result resAux = new Result(entradasProblemaVisible.get(i), codigo, salidaCorrectaProblemaVisible.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            //resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        List<InNOut> entradasProblema = problema.getEntradaOculta();
        List<InNOut> salidaCorrectaProblema = problema.getSalidaOculta();
        int numeroEntradas = entradasProblema.size();
        for (int i = 0; i < numeroEntradas; i++) {
            Result resAux = new Result(entradasProblema.get(i), codigo, salidaCorrectaProblema.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            //resultRepository.save(resAux);
            submission.addResult(resAux);
        }
        logger.info("Finish create results for submission " + submission.getId() + "\nProblem: " + problema.getId() + "\nLanguage: " + language.getNombreLenguaje());
    }

    public void ejecutaSubmission(Submission submission) {
        //Envio de mensaje a la cola
        //Envio de mensaje a la cola
        for (Result res : submission.getResults()) {
            sender.sendMessage(res);
        }
    }

    public Page<Submission> getNSubmissions(int n) {
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);
        return submissionRepository.findAll(firstPageWithTwoElements);
    }

    public String deleteSubmission(String submissionId) {
        logger.info("Delete submission " + submissionId);
        Submission submission = submissionRepository.findSubmissionById(Long.valueOf(submissionId));
        if (submission == null) {
            logger.error("Submission " + submissionId + " not found");
            return "SUBMISSION NOT FOUND";
        }

        //Comprobamos que no se este intentando borrar una SUBMISSIOn pertenciente a un SubmissionProblemValidator
        if (submission.isEsProblemValidator()) {
            logger.error("Submission " + submissionId + " is from problem validator, cannot be deleted from here");
            return "SUBMISSION IS FROM PROBLEM VALIDATOR YOU CANT DELETE IT FROM HERE, JUST DELETING DE PROBLEM";
        }
        submissionRepository.delete(submission);
        logger.info("Finish delete submission " + submissionId);
        return "OK";
    }


    public String deleteSubmission(String submissionId, String problemId, String contestId) {
        logger.info("Delete submission " + submissionId + "\nProblem: " + problemId + "\nContest: " + contestId);
        Submission submission = submissionRepository.findSubmissionById(Long.valueOf(submissionId));
        if (submission == null) {
            logger.error("Submission " + submissionId + " not found\nProblem: " + problemId + "\nContest: " + contestId);
            return "SUBMISSION NOT FOUND";
        }
        Problem problem = problemRepository.findProblemById(Long.valueOf(problemId));
        if (problem == null) {
            logger.error("Problem " + problemId + " not found");
            return "PROBLEM NOT FOUND";
        }
        Contest contest = contestRepository.findContestById(Long.valueOf(contestId));
        if (contest == null) {
            logger.error("Contest " + contestId + " not found");
            return "CONCURSO NOT FOUND";
        }

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
        logger.info("Finish delete submission " + submissionId + "\nProblem: " + problemId + "\nContest: " + contestId);
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

    public Submission getSubmission(String submissionId) {
        return submissionRepository.findSubmissionById(Long.valueOf(submissionId));
    }

    public Page<Submission> getSubmissionsPage(Pageable pageable) {
        return submissionRepository.findAll(pageable);
    }
}
