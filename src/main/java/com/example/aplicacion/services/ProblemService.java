package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Pojos.ProblemEntradaSalidaVisiblesHTML;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;


@Service
public class ProblemService {
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private InNOutRepository inNOutRepository;
    @Autowired
    private ZipHandlerService zipHandlerService;
    @Autowired
    private ProblemValidatorService problemValidatorService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ContestRepository contestRepository;
    @Autowired
     private SubmissionRepository submissionRepository;
    Logger logger = LoggerFactory.getLogger(ProblemService.class);


    public void addProblem(String nombre, List<InNOut> entrada, List<InNOut>  salidaCorrecta, List<InNOut> codigoCorrecto, List<InNOut>  entradaVisible, List<InNOut>  salidaVisible ){
        problemRepository.save(new Problem(nombre, entrada, salidaCorrecta, codigoCorrecto, entradaVisible, salidaVisible));
    }


    public ProblemString addProblemFromZip(String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        ProblemString salida = new ProblemString();
        Problem problem = new Problem();
        ProblemString problemString = new ProblemString();

        Team team =teamRepository.findTeamById(Long.valueOf(teamId));
        if (team == null) {
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        problem.setEquipoPropietario(team);
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if(contest ==null){
            salida.setSalida("CONCURSO NOT FOUND");
            return salida;
        }
        //Si el usuario introduce un nombre lo metemos a cholon
        if(!nombreProblema.equals("")){
            problemString = zipHandlerService.generateProblemFromZIP(problem, nombreFichero, inputStream, idcontest, teamId);
            problem = problemString.getProblem();
            problem.setNombreEjercicio(nombreProblema);
        }
        //Si no mete nombre cogera el que tenga en el .yml. Si no tiene en el yml cogera el nombre del archivo como nombre del problema.
        else {
            problemString = zipHandlerService.generateProblemFromZIP(problem, nombreFichero, inputStream, idcontest, teamId);
            problem = problemString.getProblem();
        }

        //Verificamos si hubiera dado fallo el problema al guardarse
        //SI FALLA NO SE GUARDA EL PROBLEMA
        if(!(problemString.getSalida()==null)){
            //problemRepository.deleteById(problem.getId());
            salida.setSalida(problemString.getSalida());
            return salida;
        }

        contest.addProblem(problem);
        problem.getListaContestsPertenece().add(contest);
        problemRepository.save(problem);

        contestRepository.save(contest);

        problemValidatorService.validateProblem(problem);
        //Devolvemos la version actualizada despues de los save
        //problem =problemRepository.findProblemById(problem.getId());

        salida.setProblem(problem);
        salida.setSalida("OK");
        return salida;

    }

    public ProblemString addProblemFromZipWithoutValidate(String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        ProblemString salida = new ProblemString();
        Problem problem = new Problem();
        ProblemString problemString = new ProblemString();

        Team team =teamRepository.findTeamById(Long.valueOf(teamId));
        if (team == null) {
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        problem.setEquipoPropietario(team);
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if(contest ==null){
            salida.setSalida("CONCURSO NOT FOUND");
            return salida;
        }
        //Si el usuario introduce un nombre lo metemos a cholon
        if(!nombreProblema.equals("")){
            problemString = zipHandlerService.generateProblemFromZIP(problem, nombreFichero, inputStream, idcontest, teamId);
            problem = problemString.getProblem();
            problem.setNombreEjercicio(nombreProblema);
        }
        //Si no mete nombre cogera el que tenga en el .yml. Si no tiene en el yml cogera el nombre del archivo como nombre del problema.
        else {
            problemString = zipHandlerService.generateProblemFromZIP(problem, nombreFichero, inputStream, idcontest, teamId);
            problem = problemString.getProblem();
        }

        //Verificamos si hubiera dado fallo el problema al guardarse
        //SI FALLA NO SE GUARDA EL PROBLEMA
        if(!(problemString.getSalida()==null)){
            //problemRepository.deleteById(problem.getId());
            salida.setSalida(problemString.getSalida());
            return salida;
        }

        contest.addProblem(problem);
        problem.getListaContestsPertenece().add(contest);


        //problemValidatorService.validateProblem(problem);
        //Devolvemos la version actualizada despues de los save
        //problem =problemRepository.findProblemById(problem.getId());

        salida.setProblem(problem);
        salida.setSalida("OK");
        return salida;

    }



    public ProblemString updateProblem(String idProblema, String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        ProblemString problemUpdated = new ProblemString();
        Problem problemOriginal = problemRepository.findProblemById(Long.valueOf(idProblema));
        if(problemOriginal == null){
            problemUpdated.setSalida("PROBLEM NOT FOUND");
            return problemUpdated;
        }
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if(contest ==null){
            problemUpdated.setSalida("CONCURSO NOT FOUND");
            return problemUpdated;
        }

        problemUpdated = addProblemFromZipWithoutValidate(nombreFichero,inputStream, teamId, nombreProblema, idcontest);
        //Si es error
        if(!problemUpdated.getSalida().equals("OK")){
            return problemUpdated;
        }


        //Tenemos que borrar el problema para poder cambiar el id
        //deleteProblem(problemUpdated.getProblem());

        //Cambiamos el id
        problemUpdated.getProblem().setId(Long.valueOf(idProblema));
        //Anyadimos al nuevo problema las submissions y problemvalidator de laanterior
        problemUpdated.getProblem().getSubmissions().addAll(problemOriginal.getSubmissions());

        //Ponemos los participantes y concursos de la anterior
        problemUpdated.getProblem().setListaEquiposIntentados(problemOriginal.getListaEquiposIntentados());
        problemUpdated.getProblem().setListaContestsPertenece(problemOriginal.getListaContestsPertenece());

        //ACTIALIZAMOS EN LA BBDD
        problemRepository.save(problemUpdated.getProblem());
        //guardamos los inNout
        saveAllInnNOut(problemUpdated.getProblem());
        saveAllSubmissions(problemUpdated.getProblem());
        contest.addProblem(problemUpdated.getProblem());
        contestRepository.save(contest);
        problemValidatorService.validateProblem(problemUpdated.getProblem());

        return problemUpdated;

    }

    public String deleteProblem(String problemId){
        Problem problem = problemRepository.findProblemById(Long.valueOf(problemId));
        if(problem==null){
            return "PROBLEM NOT FOUND";
        }

        //Quitamos los problemas del contest
        for(Contest contestAux :problem.getListaContestsPertenece()){
            contestAux.getListaProblemas().remove(problem);
        }

        problemRepository.delete(problem);
        logger.info("El problema "+problem.getNombreEjercicio()+" ha sido eliminado");
        return "OK";
    }public String deleteProblem(Problem problem){


        //Quitamos los problemas del contest
        for(Contest contestAux :problem.getListaContestsPertenece()){
            contestAux.getListaProblemas().remove(problem);
        }

        problemRepository.delete(problem);
        logger.info("El problema "+problem.getNombreEjercicio()+" ha sido eliminado");
        return "OK";
    }




    private boolean problemDuplicated(String nombre){
        return problemRepository.existsByNombreEjercicio(nombre);
    }


    public List<Problem> getNProblemas(int n){
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);

        return problemRepository.findAll();
    }
    public Problem getProblem(String idProblem){
        return problemRepository.findProblemById(Long.valueOf(idProblem));
    }
    public List<Problem> getAllProblemas(){
        List<Problem> problemas= problemRepository.findAll();
        sumatorioSubmissionProblemas(problemas);
        return problemas;
    }
    public List<Submission> getSubmissionFromProblem(Problem problem){
        List<Submission> salida = new ArrayList<>();
        salida = problem.getSubmissions();
        return salida;
    }
    public List<Submission> getSubmissionsFromContestFromProblem(Contest contest, Problem problem){
        List<Submission> salida = new ArrayList<>();
        for(Submission submission:problem.getSubmissions()){
            if(submission.getContest().equals(contest)){
                salida.add(submission);
            }
        }
        return salida;
    }

    private void sumatorioSubmissionProblemas(List<Problem> problems){
        for (Problem problem : problems){
            problem.setNumeroSubmissions(problem.getSubmissions().size());
        }
    }
    public List<ProblemEntradaSalidaVisiblesHTML> getProblemEntradaSalidaVisiblesHTML(Problem problem){
        List<ProblemEntradaSalidaVisiblesHTML> lista = new ArrayList<>();

        List<InNOut> entradasProblemaVisible = problem.getEntradaVisible();
        List<InNOut> salidaCorrectaProblemaVisible = problem.getSalidaVisible();
        int numeroEntradasVisible = entradasProblemaVisible.size();
        for(int i =0; i<numeroEntradasVisible; i++){
            ProblemEntradaSalidaVisiblesHTML problemEntradaSalidaVisiblesHTML = new ProblemEntradaSalidaVisiblesHTML();
            problemEntradaSalidaVisiblesHTML.setEntrada(entradasProblemaVisible.get(i));
            problemEntradaSalidaVisiblesHTML.setSalida(salidaCorrectaProblemaVisible.get(i));

            lista.add(problemEntradaSalidaVisiblesHTML);
        }
        return lista;
    }
    private void saveAllInnNOut(Problem problem){
        for(InNOut inNOut: problem.getEntradaVisible()){
            inNOutRepository.save(inNOut);
        }
        for(InNOut inNOut: problem.getSalidaVisible()){
            inNOutRepository.save(inNOut);
        }for(InNOut inNOut: problem.getEntradaOculta()){
            inNOutRepository.save(inNOut);
        }
        for(InNOut inNOut: problem.getSalidaOculta()){
            inNOutRepository.save(inNOut);
        }
    }
    private void saveAllSubmissions(Problem problem){
        for (SubmissionProblemValidator submissionProblemValidator: problem.getSubmissionProblemValidators()){
            submissionRepository.save(submissionProblemValidator.getSubmission());
        }
    }
}
