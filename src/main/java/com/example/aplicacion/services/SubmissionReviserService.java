package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.SubmissionProblemValidator;
import com.example.aplicacion.Repository.SubmissionProblemValidatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmissionReviserService {
    @Autowired
    private SubmissionProblemValidatorRepository submissionProblemValidatorRepository;
    @Autowired
    private ProblemValidatorService problemValidatorService;


    @Transactional
    public void revisarSubmission(Submission submission){
        if(checkAccepted(submission)){
            submission.setResultado("accepted");
        }
        submission.setCorregido(true);

        //Ahora buscamos si pudiera ser una submission de problema entrado desde ZIP
        SubmissionProblemValidator submissionProblemValidator =submissionProblemValidatorRepository.findSubmissionProblemValidatorBySubmission(submission);
        if (submissionProblemValidator!=null){
            //ME QUEDO AQUI HAY QUE anyadir q compruebe q el problema es correto
        }
    }


    private boolean checkAccepted(Submission submission){
        boolean salida = true;
        for (Result result: submission.getResults()){
            if(result.getResultadoRevision().equals("accepted")){

            }
            else {
                salida = false;
                break;
            }
        }
        return salida;
    }
}
