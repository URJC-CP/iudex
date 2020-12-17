package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Pojos.SubmissionStringResult;
import com.example.aplicacion.Repository.*;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import java.util.Optional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//This class Sends the propeer information to the rabbitqueu
@Service
public class SubmissionService {

    @Autowired
    private   RabbitTemplate rabbitTemplate;

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

    public SubmissionStringResult creaYejecutaSubmission(String codigo, String problem, String lenguaje, String fileName, String idContest , String idEquipo){
        SubmissionStringResult submissionStringResult;
        //Creamos la submission
        submissionStringResult = creaSubmission(codigo, problem, lenguaje, fileName, idContest, idEquipo);
        if(!submissionStringResult.getSalida().equals("OK")){
            return submissionStringResult;
        }
        //ejecutamos
        ejecutaSubmission(submissionStringResult.getSubmission());

        return submissionStringResult;
    }

    public SubmissionStringResult creaSubmission(String codigo, String problem, String lenguaje, String fileName, String idContest , String idEquipo){
        SubmissionStringResult submissionStringResult = new SubmissionStringResult();

        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idContest));
        if(contest.isEmpty()){
            submissionStringResult.setSalida("CONCURSO NOT FOUND");
            return submissionStringResult;
        }

        Optional<Problem> problema = problemRepository.findProblemById(Long.valueOf(problem));
        if(problema.isEmpty()){
            submissionStringResult.setSalida("PROBLEM NOT FOUND");
            return submissionStringResult;
        }

        Optional<Team> team =teamRepository.findTeamById(Long.valueOf(idEquipo));
        if (team.isEmpty()) {
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }

        Optional<Language> language  = languageRepository.findLanguageById(Long.valueOf(lenguaje));
        if(language.isEmpty()){
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        //Creamos la Submission
        Submission submission = new Submission(codigo, language.get(), fileName);
        //anadimos el probelma a la submsion
        submission.setProblema(problema.get());
        submission.setContest(contest.get());
        submission.setTeam(team.get());

        //Para que le asigne el@Id
        submissionRepository.save(submission);
        //Comprobamos q el problema pertenezca al contest
        if(!contest.get().getListaProblemas().contains(problema)){
            submissionStringResult.setSalida("PROBLEM NOT IN CONCURSO");
            return submissionStringResult;
        }

        int numeroDeResult= 0;
        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<InNOut> entradasProblemaVisible = problema.get().getEntradaVisible();
        List<InNOut> salidaCorrectaProblemaVisible = problema.get().getSalidaVisible();
        int numeroEntradasVisible = entradasProblemaVisible.size();
        for(int i =0; i<numeroEntradasVisible; i++){
            Result resAux = new Result(entradasProblemaVisible.get(i), codigo, salidaCorrectaProblemaVisible.get(i), language.get(), submission.getFilename(), problema.get().getTimeout(), problema.get().getMemoryLimit() );
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        List<InNOut> entradasProblema = problema.get().getEntradaOculta();
        List<InNOut> salidaCorrectaProblema = problema.get().getSalidaOculta();
        int numeroEntradas = entradasProblema.size();
        for(int i =0; i<numeroEntradas; i++){
            Result resAux = new Result(entradasProblema.get(i), codigo, salidaCorrectaProblema.get(i), language.get(), submission.getFilename(), problema.get().getTimeout(), problema.get().getMemoryLimit());
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        //Guardamos la submission
        problema.get().addSubmission(submission);
        problemRepository.save(problema.get());

        submissionStringResult.setSalida("OK");
        submissionStringResult.setSubmission(submission);

        return submissionStringResult;
    }

    //Constructor para ProblemValidator NO PONEMOS EL CONCURSO PARA EVITAR EL BORRADO DE LA SUBMISSION CUANDO SE BORRE EL CONCURSO Y ESE PROBLEMA TMB ESTE EN OTRO CONCURSO
    public SubmissionStringResult creaSubmissionProblemValidator(String codigo, Problem problema, String lenguaje, String fileName, String idContest , String idEquipo){
        SubmissionStringResult submissionStringResult = new SubmissionStringResult();

        /*
        Contest contest = contestRepository.findContestById(Long.valueOf(idContest));
        if(contest==null){
            submissionStringResult.setSalida("CONCURSO NOT FOUND");
            return submissionStringResult;
        }
         */


        Optional<Team> team =teamRepository.findTeamById(Long.valueOf(idEquipo));
        if (team.isEmpty()) {
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }

        Optional<Language> language  = languageRepository.findLanguageByNombreLenguaje(lenguaje);
        if(language.isEmpty()){
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        //Creamos la Submission
        Submission submission = new Submission(codigo, language.get(), fileName);
        //Para que le asigne el@Id
        //SEBORRATEMPORALMENTEsubmissionRepository.save(submission);

        //anadimos el probelma a la submsion
        submission.setProblema(problema);
        //submission.setContest(contest);
        submission.setTeam(team.get());

        int numeroDeResult =0;

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

    public void creaResults(Submission submission, Problem problema, String codigo, Language language){
        int numeroDeResult =0;

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
    }
    public void ejecutaSubmission(Submission submission){
        //Envio de mensaje a la cola
        //Envio de mensaje a la cola
        for (Result res : submission.getResults()  ) {
            sender.sendMessage(res);
        }
    }

    public Page<Submission> getNSubmissions(int n){
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);

        return submissionRepository.findAll(firstPageWithTwoElements);
    }

    public String  deleteSubmission(String submissionId){
        Optional<Submission> submission=submissionRepository.findSubmissionById(Long.valueOf(submissionId));
        if (submission.isEmpty()){
            return "SUBMISSION NOT FOUND";
        }

        //Comprobamos que no se este intentando borrar una SUBMISSIOn pertenciente a un SubmissionProblemValidator
        if(submission.get().isEsProblemValidator()){
            return "SUBMISSION IS FROM PROBLEM VALIDATOR YOU CANT DELETE IT FROM HERE, JUST DELETING DE PROBLEM";
        }
        submissionRepository.delete(submission.get());

        return "OK";
    }


    public String deleteSubmission(String submissionId, String problemId, String contestId){
        Optional<Submission> submission=submissionRepository.findSubmissionById(Long.valueOf(submissionId));
        if (submission.isEmpty()){
            return "SUBMISSION NOT FOUND";
        }
        Optional<Problem> problem = problemRepository.findProblemById(Long.valueOf(problemId));
        if(problem.isEmpty()){
            return "PROBLEM NOT FOUND";
        }
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(contestId));
        if(contest.isEmpty()){
            return "CONCURSO NOT FOUND";
        }

        if (!contest.get().getListaProblemas().contains(problem)) {
            return "CONCURSO NOT CONTAINS PROBLEM";
        }
        if(submission.get().getProblema().equals(problem)){
            return "SUBMISSION NO PERTENECE A ESTE PROBLEMA";
        }
        if(submission.get().getContest().equals(contest)){
            return "SUBMISSION NO PERTENCE A ESTE CONCURSO";
        }

        return deleteSubmission(submissionId);

    }
    public List<Submission> getAllSubmissions(){
        return submissionRepository.findAll();
    }

    public List<Submission> getSubmissionsFromContest(Contest contest){
        return contest.getListaSubmissions();
    }
    public List<Submission> getSubmissionFromProblem(Problem problem){
        return problem.getSubmissions();
    }
    public List<Submission> getSubmissionFromProblemAndContest(Problem problem, Contest contest){
        return submissionRepository.findSubmissionsByProblemaAndContest(problem, contest);
    }
    public Optional<Submission> getSubmission(String submissionId){
        return submissionRepository.findSubmissionById(Long.valueOf(submissionId));
    }
    public Page<Submission> getSubmissionsPage(Pageable pageable){
        return submissionRepository.findAll(pageable);
    }
}
