package es.urjc.etsii.grafo.iudex.service;

import es.urjc.etsii.grafo.iudex.entity.Problem;
import es.urjc.etsii.grafo.iudex.entity.Result;
import es.urjc.etsii.grafo.iudex.entity.Submission;
import es.urjc.etsii.grafo.iudex.entity.SubmissionProblemValidator;
import es.urjc.etsii.grafo.iudex.rabbitmq.RabbitResultExecutionSender;
import es.urjc.etsii.grafo.iudex.repository.ProblemRepository;
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
    private static final Logger logger = LoggerFactory.getLogger(ProblemValidatorService.class);

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitResultExecutionSender sender;

    public void validateProblem(Problem problemA) {
        Optional<Problem> problemOptional = problemRepository.findProblemById(problemA.getId());
        Problem problem = problemOptional.orElseThrow();
        //Recorremos la lista de submission y las enviamos
        if (!problem.getSubmissionProblemValidators().isEmpty()) {
            for (SubmissionProblemValidator submissionProblemValidator : problem.getSubmissionProblemValidators()) {

                Submission submission = submissionProblemValidator.getSubmission();
                logger.debug("Validate submission {} for problem {}", submission.getId(), problem.getId());

                //Ejecutamos
                if (submission.getLanguage() != null) {
                    for (Result res : submission.getResults()) {
                        logger.debug("Sending result {} of submission {}", res.getId(), submission.getId());
                        sender.sendMessage(res);
                        logger.debug("Finish send result {} of submission {}", res.getId(), submission.getId());
                    }
                } else {
                    logger.error("Unsupported language");
                }
            }
        }
        //Si es un problema sin submission validamos
        else {
            problem.setValido(true);
            logger.debug("Finish validate problem {} without test case", problem.getNombreEjercicio());
            problemRepository.save(problem);
        }
    }

    public void checkIfProblemFinishedAndDoValidateIt(SubmissionProblemValidator submissionProblemValidator) {
        long problemId = submissionProblemValidator.getSubmission().getProblema().getId();

        //Buscamos el problema en la BBDD para estar seguros de que esta actualizado
        Optional<Problem> problemOptional = problemRepository.findById(problemId);
        Problem problem = problemOptional.orElseThrow();
        logger.debug("Checking and validating problem {}", problem.getNombreEjercicio());

        //Buscamos todas las submssions del problema y en caso de que haya una que no este terminada lo marcamos
        boolean estaTerminado = true;
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
                logger.debug("Finish validate problem {}", problem.getNombreEjercicio());
            } else {
                problem.setValido(false);
                logger.warn("Invalid problem {}", problem.getNombreEjercicio());
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
                logger.info("Unexpected result in submission {}", submissionProblemValidator.getSubmission().getId());
                logger.info("Expected: {} but Given: {}", submissionProblemValidator.getExpectedSolution(), aux);
            }
        }
        return salida;
    }
}
