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
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
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
    private ConcursoRepository concursoRepository;

    @Autowired
    private RabbitResultExecutionSender sender;

    public String creaYejecutaSubmission(String codigo, String problem, String lenguaje, String fileName, String idEquipo , String idConcurso){
        String salida = creaSubmission(codigo, problem, lenguaje, fileName, idEquipo, idConcurso);

        return salida;
    }

    public SubmissionStringResult creaSubmission(String codigo, String problem, String lenguaje, String fileName, String idEquipo , String idConcurso){
        SubmissionStringResult submissionStringResult = new SubmissionStringResult();

        //Obtedemos el Problema del que se trata
        Problem problema = problemRepository.findProblemByNombreEjercicio(problem);
        if(problema ==null){
            submissionStringResult.setSalida("PROBLEM NOT FOUND");
            return submissionStringResult;
        }
        Language language  = languageRepository.findLanguageByNombreLenguaje(lenguaje);
        if(language==null){
            submissionStringResult.setSalida("LANGUAGE NOT FOUND");
            return submissionStringResult;
        }
        //Creamos la Submission
        Submission submission = new Submission(codigo, language, fileName, true);

        //anadimos el probelma a la submsion
        submission.setProblema(problema);

        Concurso concurso = concursoRepository.findConcursoById(Long.valueOf(idConcurso));
        if(concurso==null){
            submissionStringResult.setSalida("CONCURSO NOT FOUND");
            return submissionStringResult;
        }
        submission.setConcurso(concurso);

        //Comprobamos q el problema pertenezca al concurso
        if(!concurso.getListaProblemas().contains(problema)){
            submissionStringResult.setSalida("PROBLEM NOT IN CONCURSO");
            return submissionStringResult;
        }

        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<InNOut> entradasProblemaVisible = problema.getEntradaVisible();
        List<InNOut> salidaCorrectaProblemaVisible = problema.getSalidaVisible();
        int numeroEntradasVisible = entradasProblemaVisible.size();
        for(int i =0; i<numeroEntradasVisible; i++){
            Result resAux = new Result(entradasProblemaVisible.get(i), codigo, salidaCorrectaProblemaVisible.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit() );
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        List<InNOut> entradasProblema = problema.getEntradaOculta();
        List<InNOut> salidaCorrectaProblema = problema.getSalidaOculta();
        int numeroEntradas = entradasProblema.size();
        for(int i =0; i<numeroEntradas; i++){
            Result resAux = new Result(entradasProblema.get(i), codigo, salidaCorrectaProblema.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        Team team =teamRepository.findTeamById(Long.valueOf(idEquipo));
        if (team == null) {
            submissionStringResult.setSalida("TEAM NOT FOUND");
            return submissionStringResult;
        }
        submission.setTeam(team);
        //Guardamos la submission
        submissionRepository.save(submission);

        problema.addSubmission(submission);
        problemRepository.save(problema);

        
        submissionStringResult.setSalida("OK");
        submissionStringResult.setSubmission(submission);

        return submissionStringResult;
    }
    public void ejecutaSubmission(Submission submission){
        //Envio de mensaje a la cola
        for (Result res : submission.getResults()  ) {
            sender.sendMessage(res);
        }
    }

    public Page<Submission> getNSubmissions(int n){
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);

        return submissionRepository.findAll(firstPageWithTwoElements);
    }

    public String deleteSubmission(String submissionId, String problemId, String concursoId){
        Submission submission=submissionRepository.findSubmissionById(Long.valueOf(submissionId));
        if (submission==null){
            return "SUBMISSION NOT FOUND";
        }
        Problem problem = problemRepository.findProblemById(Long.valueOf(problemId));
        if(problem==null){
            return "PROBLEM NOT FOUND";
        }
        Concurso concurso = concursoRepository.findConcursoById(Long.valueOf(concursoId));
        if(concurso==null){
            return "CONCURSO NOT FOUND";
        }

        if (!concurso.getListaProblemas().contains(problem)) {
            return "CONCURSO NOT CONTAINS PROBLEM";
        }
        if(submission.getProblema().equals(problem)){
            return "SUBMISSION NO PERTENECE A ESTE PROBLEMA";
        }
        if(submission.getConcurso().equals(concurso)){
            return "SUBMISSION NO PERTENCE A ESTE CONCURSO";
        }


        submissionRepository.delete(submission);

        return "OK";
    }
    public List<Submission> getAllSubmissions(){
        return submissionRepository.findAll();
    }

}
