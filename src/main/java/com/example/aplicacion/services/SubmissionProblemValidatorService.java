package com.example.aplicacion.services;

import com.example.aplicacion.entities.Problem;
import com.example.aplicacion.entities.SubmissionProblemValidator;
import com.example.aplicacion.pojos.SubmissionStringResult;
import com.example.aplicacion.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmissionProblemValidatorService {
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
    private ContestService contestService;
    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private TeamRepository teamRepository;
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
