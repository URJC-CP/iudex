package com.example.aplicacion.services;

import com.example.aplicacion.entities.Problem;
import com.example.aplicacion.entities.SubmissionProblemValidator;
import com.example.aplicacion.pojos.SubmissionStringResult;
import org.springframework.stereotype.Service;

@Service
public class SubmissionProblemValidatorService extends BaseService {
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
