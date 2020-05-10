package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Repository.*;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemValidatorService {

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
    private SubmissionProblemValidatorRepository submissionProblemValidatorRepository;

    @Autowired
    private RabbitResultExecutionSender sender;


    public void validateProblem(Problem problem){
        for(SubmissionProblemValidator submissionProblemValidator: problem.getSubmissionProblemValidators()){

            Submission submission= submissionProblemValidator.getSubmission();
            List<InNOut> entradasProblemaVisible = problem.getEntradaVisible();
            List<InNOut> salidaCorrectaProblemaVisible = problem.getSalidaVisible();

            int numeroEntradasVisible = entradasProblemaVisible.size();
            for(int i =0; i<numeroEntradasVisible; i++){
                Result resAux = new Result(entradasProblemaVisible.get(i), submission.getCodigo(), salidaCorrectaProblemaVisible.get(i), submission.getLanguage(), submission.getFilename(), problem.getTimeout(), problem.getMemoryLimit() );
                resultRepository.save(resAux);
                submission.addResult(resAux);
            }

            List<InNOut> entradasProblema = problem.getEntradaOculta();
            List<InNOut> salidaCorrectaProblema = problem.getSalidaOculta();
            int numeroEntradas = entradasProblema.size();
            for(int i =0; i<numeroEntradas; i++){
                Result resAux = new Result(entradasProblema.get(i), submission.getCodigo(), salidaCorrectaProblema.get(i), submission.getLanguage(), submission.getFilename(), problem.getTimeout(), problem.getMemoryLimit());
                resultRepository.save(resAux);
                submission.addResult(resAux);
            }


            //Guardamos la submission
            submissionProblemValidatorRepository.save(submissionProblemValidator);


            switch (submission.getLanguage().getNombreLenguaje()){
                case "java":
                    for (Result res : submission.getResults()  ) {
                        sender.sendMessage(res);
                    }
                    break;

            }

        }


    }

    //checkea que la submission del problema den el resultado q tienen que dar
    public boolean checkSubmissionResult(Problem problem){
        boolean salida = true;

        for(SubmissionProblemValidator submissionProblemValidator: problem.getSubmissionProblemValidators()){
            Submission submission = submissionProblemValidator.getSubmission();
            waitForResult(submissionProblemValidator);  //Esperamos q este corregido

            System.out.println("Ha pasado el while q espera a q este todo terminado");
            //Si es accepted comprueba que es asi y ha salido correctamente
            if(submissionProblemValidator.getExpectedSolution().equals("accepted")){
                if(submission.getResultado().equals("accepted")){

                }
                else {
                    //Si no coincide lo ponemos a false. DEBERIA TIRAR ERROR
                    salida = false;
                }
            }
        }


        return salida;
    }
    private void waitForResult(SubmissionProblemValidator submissionProblemValidator){
        Submission submission = submissionProblemValidator.getSubmission();
        while(!submission.isCorregido()){
            try {
                Thread.sleep(10000);
                submission = submissionRepository.findSubmissionById(submission.getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
