package es.urjc.etsii.grafo.iudex.service;

import es.urjc.etsii.grafo.iudex.entity.Problem;
import es.urjc.etsii.grafo.iudex.entity.SubmissionProblemValidator;
import es.urjc.etsii.grafo.iudex.pojo.SubmissionStringResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmissionProblemValidatorService {
    @Autowired
    private SubmissionService submissionService;

    public SubmissionProblemValidator createSubmissionNoExecute(String codigo, Problem problema, String lenguaje, String fileName, String expectedResult, String idEquipo) {
        SubmissionProblemValidator submissionProblemValidator = new SubmissionProblemValidator();
        submissionProblemValidator.setExpectedSolution(expectedResult);

        //Creamos la submission
        SubmissionStringResult submissionStringResult = submissionService.creaSubmissionProblemValidator(codigo, problema, lenguaje, fileName, idEquipo);
        if (!submissionStringResult.getSalida().equals("OK")) {
            //REVISAR
            return null;
        }
        submissionProblemValidator.setSubmission(submissionStringResult.getSubmission());

        return submissionProblemValidator;
    }
}
