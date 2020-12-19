package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.SubmissionProblemValidator;
import com.example.aplicacion.Pojos.SubmissionStringResult;
import com.example.aplicacion.Repository.*;
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

    public SubmissionProblemValidator createSubmissionNoExecute(String codigo, Problem problema, String lenguaje, String fileName, String expectedResult, String idContest, String idEquipo) {
        SubmissionProblemValidator submissionProblemValidator = new SubmissionProblemValidator();
        submissionProblemValidator.setExpectedSolution(expectedResult);

        //Creamos la submission
        SubmissionStringResult submissionStringResult = submissionService.creaSubmissionProblemValidator(codigo, problema, lenguaje, fileName, idContest, idEquipo);
        if (!submissionStringResult.getSalida().equals("OK")) {
            //REVISAR
            return null;
        }
        submissionProblemValidator.setSubmission(submissionStringResult.getSubmission());

        return submissionProblemValidator;
    }

    /*
    //clase que crea los results y los services dentro de un submission
    public SubmissionProblemValidator createSubmissionNoExecute(String codigo, Problem problema, String lenguaje, String fileName , String expectedResult, String idContest, String idEquipo){
        //problemRepository.save(problema);
        Contest contest = contestRepository.findContestById(Long.valueOf(idContest));
        if(contest==null){
            //return "CONCURSO NOT FOUND";
        }
        submission.setContest(contest);

        Team team =teamRepository.findTeamById(Long.valueOf(idEquipo));
        if (team == null) {
            //return "TEAM NOT FOUND";
        }
        //Comprobamos q el problema pertenezca al contest
        if(!contest.getListaProblemas().contains(problema)){
            //return "PROBLEM NOT IN CONCURSO";
        }

        //Obtedemos el Problema del que se trata
        Language language  = languageRepository.findLanguageByNombreLenguaje(lenguaje);
        //Creamos la Submission
        SubmissionProblemValidator submissionProblemValidator = new SubmissionProblemValidator(codigo, language, fileName, expectedResult);
        Submission submission = submissionProblemValidator.getSubmission();


        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<InNOut> entradasProblemaVisible = problema.getEntradaVisible();
        List<InNOut> salidaCorrectaProblemaVisible = problema.getSalidaVisible();
        int numeroEntradasVisible = entradasProblemaVisible.size();
        for(int i =0; i<numeroEntradasVisible; i++){
            Result resAux = new Result(entradasProblemaVisible.get(i), codigo, salidaCorrectaProblemaVisible.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit() );
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        List<InNOut> entradasProblema = problema.getEntradaOculta();
        List<InNOut> salidaCorrectaProblema = problema.getSalidaOculta();
        int numeroEntradas = entradasProblema.size();
        for(int i =0; i<numeroEntradas; i++){
            Result resAux = new Result(entradasProblema.get(i), codigo, salidaCorrectaProblema.get(i), language, submission.getFilename(), problema.getTimeout(), problema.getMemoryLimit());
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }
        submission.setProblema(problema);
        problema.generaHash();
        submission.generaHashProblema();

        //submissionRepository.save(submissionProblemValidator.getSubmission());
        //submissionProblemValidatorRepository.save(submissionProblemValidator);

        return submissionProblemValidator;
    }
     */
}
