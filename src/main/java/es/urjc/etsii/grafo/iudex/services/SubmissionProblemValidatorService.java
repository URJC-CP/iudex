package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.Problem;
import es.urjc.etsii.grafo.iudex.entities.SubmissionProblemValidator;
import es.urjc.etsii.grafo.iudex.pojos.SubmissionStringResult;
import org.springframework.stereotype.Service;

@Service
public class SubmissionProblemValidatorService {
    private final SubmissionService submissionService;

    public SubmissionProblemValidatorService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public SubmissionProblemValidator createSubmissionNoExecute(String codigo, Problem problema, String lenguaje, String fileName, String expectedResult, String idEquipo, String contestId) {
        SubmissionProblemValidator submissionProblemValidator = new SubmissionProblemValidator();
        submissionProblemValidator.setExpectedSolution(expectedResult);

        //Creamos la submission
        SubmissionStringResult submissionStringResult = submissionService.creaSubmissionProblemValidator(codigo, problema, lenguaje, fileName, idEquipo, contestId);
        if (!submissionStringResult.getSalida().equals("OK")) {
            //REVISAR
            return null;
        }
        submissionProblemValidator.setSubmission(submissionStringResult.getSubmission());

        return submissionProblemValidator;
    }
}
