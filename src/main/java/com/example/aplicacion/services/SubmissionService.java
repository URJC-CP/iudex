package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Pojos.SubmissionStringResult;
import com.example.aplicacion.Repository.*;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
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

    public String creaYejecutaSubmission(String codigo, String problem, String lenguaje, String fileName, String idContest , String idEquipo){
        //Creamos la submission
        SubmissionStringResult submissionStringResult = creaSubmission(codigo, problem, lenguaje, fileName, idContest, idEquipo);
        if(!submissionStringResult.getSalida().equals("OK")){
            return submissionStringResult.getSalida();
        }
        //ejecutamos
        ejecutaSubmission(submissionStringResult.getSubmission());
        return "OK";
    }

    public SubmissionStringResult creaSubmission(String codigo, String problem, String lenguaje, String fileName, String idContest , String idEquipo){
        SubmissionStringResult submissionStringResult = new SubmissionStringResult();

        Contest contest = contestRepository.findContestById(Long.valueOf(idContest));
        if(contest ==null){
            submissionStringResult.setSalida("CONCURSO NOT FOUND");
            return submissionStringResult;
        }

        Problem problema = problemRepository.findProblemById(Long.valueOf(problem));
        if(problema ==null){
            submissionStringResult.setSalida("PROBLEM NOT FOUND");
            return submissionStringResult;
        }
        Team team =teamRepository.findTeamById(Long.valueOf(idEquipo));
        if (team == null) {
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }

        Language language  = languageRepository.findLanguageById(Long.valueOf(lenguaje));
        if(language==null){
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
        if(!contest.getListaProblemas().contains(problema)){
            submissionStringResult.setSalida("PROBLEM NOT IN CONCURSO");
            return submissionStringResult;
        }

        int numeroDeResult= 0;
        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<InNOut> entradasProblemaVisible = problema.getEntradaVisible();
        List<InNOut> salidaCorrectaProblemaVisible = problema.getSalidaVisible();
        int numeroEntradasVisible = entradasProblemaVisible.size();
        for(int i =0; i<numeroEntradasVisible; i++){
            Result resAux = new Result(entradasProblemaVisible.get(i), codigo, salidaCorrectaProblemaVisible.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit() );
            resAux.setNumeroCasoDePrueba(numeroDeResult);
            numeroDeResult++;
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        List<InNOut> entradasProblema = problema.getEntradaOculta();
        List<InNOut> salidaCorrectaProblema = problema.getSalidaOculta();
        int numeroEntradas = entradasProblema.size();
        for(int i =0; i<numeroEntradas; i++){
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


        Team team =teamRepository.findTeamById(Long.valueOf(idEquipo));
        if (team == null) {
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }

        Language language  = languageRepository.findLanguageByNombreLenguaje(lenguaje);
        if(language==null){
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        //Creamos la Submission
        Submission submission = new Submission(codigo, language, fileName);
        //Para que le asigne el@Id
        submissionRepository.save(submission);

        //anadimos el probelma a la submsion
        submission.setProblema(problema);
        //submission.setContest(contest);
        submission.setTeam(team);


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


        submission.setEsProblemValidator(true);
        //Guardamos la submission

        problema.addSubmission(submission);
        problemRepository.save(problema);


        submissionStringResult.setSalida("OK");
        submissionStringResult.setSubmission(submission);
        return submissionStringResult;
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
        Submission submission=submissionRepository.findSubmissionById(Long.valueOf(submissionId));
        if (submission==null){
            return "SUBMISSION NOT FOUND";
        }

        //Comprobamos que no se este intentando borrar una SUBMISSIOn pertenciente a un SubmissionProblemValidator
        if(submission.isEsProblemValidator()){
            return "SUBMISSION IS FROM PROBLEM VALIDATOR YOU CANT DELETE IT FROM HERE, JUST DELETING DE PROBLEM";
        }


        submissionRepository.delete(submission);

        return "OK";
    }
    public String deleteSubmission(String submissionId, String problemId, String contestId){
        Submission submission=submissionRepository.findSubmissionById(Long.valueOf(submissionId));
        if (submission==null){
            return "SUBMISSION NOT FOUND";
        }
        Problem problem = problemRepository.findProblemById(Long.valueOf(problemId));
        if(problem==null){
            return "PROBLEM NOT FOUND";
        }
        Contest contest = contestRepository.findContestById(Long.valueOf(contestId));
        if(contest ==null){
            return "CONCURSO NOT FOUND";
        }

        if (!contest.getListaProblemas().contains(problem)) {
            return "CONCURSO NOT CONTAINS PROBLEM";
        }
        if(submission.getProblema().equals(problem)){
            return "SUBMISSION NO PERTENECE A ESTE PROBLEMA";
        }
        if(submission.getContest().equals(contest)){
            return "SUBMISSION NO PERTENCE A ESTE CONCURSO";
        }

        return deleteSubmission(submissionId);

    }
    public List<Submission> getAllSubmissions(){
        return submissionRepository.findAll();
    }

}
