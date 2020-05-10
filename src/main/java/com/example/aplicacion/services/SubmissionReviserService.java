package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Submission;
import org.springframework.stereotype.Service;

@Service
public class SubmissionReviserService {

    public void revisarSubmission(Submission submission){
        if(checkAccepted(submission)){
            submission.setResultado("accepted");
        }
        submission.setCorregido(true);
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
