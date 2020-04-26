package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Repository.*;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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



    public SubmissionProblemValidator createSubmissionNoExecute(String codigo, Problem problema, String lenguaje, String fileName , String expectedResult){
        problemRepository.save(problema);


        //Obtedemos el Problema del que se trata
        Language language  = languageRepository.findLanguageByNombreLenguaje(lenguaje);
        //Creamos la Submission
        SubmissionProblemValidator submissionProblemValidator = new SubmissionProblemValidator(codigo, language, fileName, expectedResult);
        //anadimos el probelma a la submsion
        submissionProblemValidator.setProblema(problema);
        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<InNOut> entradasProblemaVisible = problema.getEntradaVisible();
        List<InNOut> salidaCorrectaProblemaVisible = problema.getSalidaVisible();
        int numeroEntradasVisible = entradasProblemaVisible.size();
        for(int i =0; i<numeroEntradasVisible; i++){
            Result resAux = new Result(entradasProblemaVisible.get(i), codigo, salidaCorrectaProblemaVisible.get(i), language, submissionProblemValidator.getFilename(), problema.getTimeout(), problema.getMemoryLimit() );
            resultRepository.save(resAux);
            submissionProblemValidator.addResult(resAux);
        }

        List<InNOut> entradasProblema = problema.getEntradaOculta();
        List<InNOut> salidaCorrectaProblema = problema.getSalidaOculta();
        int numeroEntradas = entradasProblema.size();
        for(int i =0; i<numeroEntradas; i++){
            Result resAux = new Result(entradasProblema.get(i), codigo, salidaCorrectaProblema.get(i), language, submissionProblemValidator.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resultRepository.save(resAux);
            submissionProblemValidator.addResult(resAux);
        }
        submissionProblemValidatorRepository.save(submissionProblemValidator);

        return submissionProblemValidator;
    }
}
