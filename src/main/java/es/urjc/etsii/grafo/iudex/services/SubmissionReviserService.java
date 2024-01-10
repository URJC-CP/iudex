package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.Result;
import es.urjc.etsii.grafo.iudex.entities.Submission;
import es.urjc.etsii.grafo.iudex.entities.SubmissionProblemValidator;
import es.urjc.etsii.grafo.iudex.events.publishers.IudexSpecificContestSubmissionEventPublisher;
import es.urjc.etsii.grafo.iudex.events.publishers.IudexSpecificContestTeamSubmissionEventPublisher;
import es.urjc.etsii.grafo.iudex.events.types.IudexSubmissionEvent;
import es.urjc.etsii.grafo.iudex.pojos.SubmissionAPI;
import es.urjc.etsii.grafo.iudex.repositories.SubmissionProblemValidatorRepository;
import es.urjc.etsii.grafo.iudex.repositories.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//Clase que se encarga de comprobar el resultado de una submission cuando esta ha finalizado
@Service
public class SubmissionReviserService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionReviserService.class);

    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private SubmissionProblemValidatorRepository submissionProblemValidatorRepository;

    @Autowired
    private ProblemValidatorService problemValidatorService;

    @Autowired
    private IudexSpecificContestSubmissionEventPublisher iudexSpecificContestSubmissionEventPublisher;

    @Autowired
    private IudexSpecificContestTeamSubmissionEventPublisher iudexSpecificContestTeamSubmissionEventPublisher;

    @Transactional
    //Metodo que revisa si una submission ha sido aceptada y si no, indica el primero de los errores que ha dado
    public void revisarSubmission(Submission submission) {
        logger.info("Review submission {}", submission.getId());

        //HAY QUE HACERLO CON EL RESTO DE OPCIONES WRONG ANSWER ETCETC
        submission.setCorregido(true);

        if (checkAccepted(submission)) {
            submission.setResult("accepted");
            submissionRepository.save(submission);

            SubmissionAPI submissionAPI = submission.toSubmissionAPISimple();
            submissionAPI.setProblem(submission.getProblem().toProblemAPISimple());

            iudexSpecificContestSubmissionEventPublisher.sendMessage(new IudexSubmissionEvent(submissionAPI), submission.getContest().getId());
        } else {
            submission.setResult(checkSubmission(submission));
            submissionRepository.save(submission);

            SubmissionAPI submissionAPI = submission.toSubmissionAPISimple();
            submissionAPI.setProblem(submission.getProblem().toProblemAPISimple());

            iudexSpecificContestTeamSubmissionEventPublisher.sendMessage(new IudexSubmissionEvent(submissionAPI), submission.getContest().getId(), submission.getTeam().getId());
        }


        //Ahora buscamos si pudiera ser una submission de problema entrado desde ZIP
        Optional<SubmissionProblemValidator> submissionProblemValidatorOptional = submissionProblemValidatorRepository.findSubmissionProblemValidatorBySubmission(submission);
        if (submissionProblemValidatorOptional.isPresent()) {
            SubmissionProblemValidator submissionProblemValidator = submissionProblemValidatorOptional.get();
            //En caso de que sea una submission de la entrada de un problema ejecutaremos el metodo que controlara que cuando todas las submission de dicho probelma ha terminado
            //Se valide el problema y pueda usarse en la aplicacion
            problemValidatorService.checkIfProblemFinishedAndDoValidateIt(submissionProblemValidator);
        }
        logger.info("Finish review submission {} with {}", submission.getId(), submission.getResult());
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
                salida = result.getResultadoRevision();
                break;
            }
        }
        return salida;
    }
}
