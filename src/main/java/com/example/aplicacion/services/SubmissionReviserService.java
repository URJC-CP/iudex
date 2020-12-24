package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.SubmissionProblemValidator;
import com.example.aplicacion.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//Clase que se encarga de comprobar el resultado de una submission cuando esta ha finalizado
@Service
public class SubmissionReviserService {
    Logger logger = LoggerFactory.getLogger(SubmissionReviserService.class);
    @Autowired
    private SubmissionProblemValidatorRepository submissionProblemValidatorRepository;
    @Autowired
    private ProblemValidatorService problemValidatorService;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private LanguageRepository languageRepository;

    //Hay que anyadirlo por que da fallo al no obtener todo en la BBDD haciendolo mediante evaluacion perezosa. Esto elimina esa variable de la lista
    @Transactional
    //Metodo que revisa si una submission ha sido aceptada y si no, indica el primero de los errores que ha dado
    public void revisarSubmission(Submission submission) {
        logger.info("Review submission " + submission.getId());

        if (checkAccepted(submission)) {
            submission.setResultado("accepted");
        } else {
            submission.setResultado(checkSubmission(submission));
        }

        //HAY QUE HACERLO CON EL RESTO DE OPCIONES WRONG ANSWER ETCETC

        submission.setCorregido(true);
        submissionRepository.save(submission);

        //Ahora buscamos si pudiera ser una submission de problema entrado desde ZIP
        Optional<SubmissionProblemValidator> submissionProblemValidator = submissionProblemValidatorRepository.findSubmissionProblemValidatorBySubmission(submission);
        if (submissionProblemValidator.isPresent()) {
            //En caso de que sea una submission de la entrada de un problema ejecutaremos el metodo que controlara que cuando todas las submission de dicho probelma ha terminado
            //Se valide el problema y pueda usarse en la aplicacion
            problemValidatorService.checkIfProblemFinishedAndDoValidateIt(submissionProblemValidator.get());
        }
        logger.info("Finish review submission " + submission.getId()+" with "+submission.getResultado());
    }

    //Chekea si esta aceptado
    private boolean checkAccepted(Submission submission) {
        boolean salida = true;
        for (Result result : submission.getResults()) {

            if (result.getResultadoRevision().equals("accepted")) {
                //Sumamos los tiempos de ejecucion
                submission.setExecSubmissionTime(submission.getExecSubmissionTime() + result.getExecTime());
                submission.setExecSubmissionMemory(submission.getExecSubmissionMemory() + result.getExecMemory());

            } else {
                salida = false;
                break;
            }
        }
        return salida;
    }

    //Obtiene el primer error
    private String checkSubmission(Submission submission) {
        String salida = "";

        for (Result result : submission.getResults()) {
            if (result.getResultadoRevision().equals("accepted")) {
                //Sumamos los tiempos de ejecucion
                submission.setExecSubmissionTime(submission.getExecSubmissionTime() + result.getExecTime());
                submission.setExecSubmissionMemory(submission.getExecSubmissionMemory() + result.getExecMemory());
            } else {
                //Obtenemos el primero de los errores
                String aux = result.getResultadoRevision();
                salida = aux;
                break;
            }
        }

        return salida;
    }
}
