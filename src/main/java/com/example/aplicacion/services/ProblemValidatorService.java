package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.SubmissionProblemValidator;
import com.example.aplicacion.Repository.*;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

//Clase que valida que el problema introducido sea correcto. Primero ejecuta el problema y luego comprueba que los resultados son los q tienen q ser
@Service

public class ProblemValidatorService {

    Logger logger = LoggerFactory.getLogger(ProblemValidatorService.class);
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

    public void validateProblem(Problem problemA) {
        Optional<Problem> problemOptional = problemRepository.findProblemById(problemA.getId());

        Problem problem = problemOptional.get();
        //Recorremos la lista de submission y las enviamos
        if (problem.getSubmissionProblemValidators().size() != 0) {
            for (SubmissionProblemValidator submissionProblemValidator : problem.getSubmissionProblemValidators()) {

                Submission submission = submissionProblemValidator.getSubmission();
                logger.debug("Validate submission " + submission.getId() + "\nProblem: " + problem.getId() + ", " + problem.getNombreEjercicio());

                //Ejecutamos
                if (submission.getLanguage() != null) {
                    for (Result res : submission.getResults()) {
                        logger.debug("Send result " + res.getId() + " of submission " + submission.getId());
                        sender.sendMessage(res);
                        logger.debug("Finish send result " + res.getId() + " of submission " + submission.getId());
                    }
                } else {
                    logger.error("Unsupported language " + submission.getLanguage());
                }
            }
        }
        //Si es un problema sin submission validamos
        else {
            problem.setValido(true);
            logger.debug("Finish validate problem " + problem.getNombreEjercicio() + " without test case");
            problemRepository.save(problem);
        }
    }

    public void checkIfProblemFinishedAndDoValidateIt(SubmissionProblemValidator submissionProblemValidator) {
        long problemId = submissionProblemValidator.getSubmission().getProblema().getId();

        //Buscamos el problema en la BBDD para estar seguros de que esta actualizado
        Optional<Problem> problemOptional = problemRepository.findById(problemId);
        Problem problem = problemOptional.get();
        logger.debug("Check problem " + problem.getNombreEjercicio());

        //Buscamos todas las submssions del problema y en caso de que haya una que no este terminada lo marcamos
        Boolean estaTerminado = true;
        for (SubmissionProblemValidator submissionProblemValidator1 : problem.getSubmissionProblemValidators()) {
            if (submissionProblemValidator1.getSubmission().isTerminadoDeEjecutarResults()) {
            } else {  //Aun no ha terminado
                estaTerminado = false;
                break;
            }
        }

        //Si esta terminado ejecutaremos que el resultado correspondiente de cada submission es el q tiene q ser, q los accepted sean aceepted etcetc
        if (estaTerminado) {
            //En caso de que sea valido lo apuntamos
            if (checkSubmissionResultIsValide(problem)) {
                problem.setValido(true);
                logger.debug("Finish validate problem " + problem.getNombreEjercicio());
            } else {
                problem.setValido(false);
                logger.warn("Invalid problem " + problem.getNombreEjercicio());
            }
            problemRepository.save(problem);
        }
    }

    //checkea que la submission del problema den el resultado q tienen que dar
    private boolean checkSubmissionResultIsValide(Problem problem) {
        boolean salida = true;

        for (SubmissionProblemValidator submissionProblemValidator : problem.getSubmissionProblemValidators()) {
            Submission submission = submissionProblemValidator.getSubmission();

            //Obtenemos la primera linea del resultado del Submission
            String aux = submission.getResultado();
            try {
                aux = new BufferedReader(new StringReader(aux)).readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Si el resultado esperado es igual al obtenido devolvemos true si no false
            if (!submissionProblemValidator.getExpectedSolution().equals(aux)) {
                salida = false;
                logger.info("Unexpected result in submission " + submissionProblemValidator.getSubmission().getId());
                logger.info("Expected result: " + submissionProblemValidator.getExpectedSolution() + "\nGiven result: " + aux);
            }
        }
        return salida;
    }
}
