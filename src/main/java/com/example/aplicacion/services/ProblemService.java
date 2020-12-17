package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Pojos.ProblemEntradaSalidaVisiblesHTML;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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


    public ProblemString addProblem(Problem createdProblem){
        ProblemString problemString = new ProblemString();
        Problem newProblem = new Problem();

        updateProblemInside(newProblem, createdProblem);

        newProblem.getListaContestsPertenece().addAll(createdProblem.getListaContestsPertenece());
        newProblem.setEquipoPropietario(createdProblem.getEquipoPropietario());
        newProblem.getListaEquiposIntentados().addAll(createdProblem.getListaEquiposIntentados());


        problemRepository.save(newProblem);
        problemValidatorService.validateProblem(newProblem);

        problemString.setSalida("OK");
        problemString.setProblem(newProblem);

        return problemString;
    }


    public ProblemString addProblemFromZip(String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        ProblemString salida = new ProblemString();
        Problem problem = new Problem();
        ProblemString problemString = new ProblemString();

        Optional<Team> team =teamRepository.findTeamById(Long.valueOf(teamId));
        if (team.isEmpty()) {
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        problem.setEquipoPropietario(team.get());
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if(contest.isEmpty()){
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

        contest.get().addProblem(problem);
        problem.getListaContestsPertenece().add(contest.get());
        problemRepository.save(problem);

        contestRepository.save(contest.get());

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

        Optional<Team> team =teamRepository.findTeamById(Long.valueOf(teamId));
        if (team.isEmpty()) {
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        problem.setEquipoPropietario(team.get());
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if(contest.isEmpty()){
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

        //contest.addProblem(problem);
        //problem.getListaContestsPertenece().add(contest);

        //problemValidatorService.validateProblem(problem);
        //Devolvemos la version actualizada despues de los save
        //problem =problemRepository.findProblemById(problem.getId());

        salida.setProblem(problem);
        salida.setSalida("OK");
        return salida;

    }



    public ProblemString updateProblem(String idProblema, String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        ProblemString problemUpdated = new ProblemString();
        Optional<Problem> problemOriginal = problemRepository.findProblemById(Long.valueOf(idProblema));
        if(problemOriginal.isEmpty()){
            problemUpdated.setSalida("PROBLEM NOT FOUND");
            return problemUpdated;
        }

        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if(contest.isEmpty()){
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
        problemUpdated.getProblem().getSubmissions().addAll(problemOriginal.get().getSubmissions());

        //Guardams los problemvalidator viejos
        problemUpdated.getProblem().setOldSubmissionProblemValidators(problemOriginal.get().getOldSubmissionProblemValidators());
        problemUpdated.getProblem().getOldSubmissionProblemValidators().addAll(problemOriginal.get().getSubmissionProblemValidators());

        //Ponemos los participantes y concursos de la anterior
        problemUpdated.getProblem().setListaEquiposIntentados(problemOriginal.get().getListaEquiposIntentados());
        problemUpdated.getProblem().setListaContestsPertenece(problemOriginal.get().getListaContestsPertenece());

        //ACTIALIZAMOS EN LA BBDD
        problemRepository.save(problemUpdated.getProblem());
        problemValidatorService.validateProblem(problemUpdated.getProblem());

        return problemUpdated;
    }

    public ProblemString updateProblem2(String idProblema, String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        ProblemString problemUpdated = new ProblemString();

        Optional<Problem> problemOriginal = problemRepository.findProblemById(Long.valueOf(idProblema));
        if(problemOriginal.isEmpty()){
            problemUpdated.setSalida("PROBLEM NOT FOUND");
            return problemUpdated;
        }

        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if(contest.isEmpty()){
            problemUpdated.setSalida("CONCURSO NOT FOUND");
            return problemUpdated;
        }

        problemUpdated = addProblemFromZipWithoutValidate(nombreFichero,inputStream, teamId, nombreProblema, idcontest);
        //Si es error
        if(!problemUpdated.getSalida().equals("OK")){
            return problemUpdated;
        }

        updateProblemInside(problemOriginal.get(), problemUpdated.getProblem());
        //saveAllInnNOut(problemOriginal);

        problemRepository.save(problemOriginal.get());
        problemValidatorService.validateProblem(problemOriginal.get());

        problemUpdated.setProblem(problemOriginal.get());
        return problemUpdated;

    }

    public String deleteProblem(String problemId){
        Optional<Problem> problem = problemRepository.findProblemById(Long.valueOf(problemId));
        if(problem.isEmpty()){
            return "PROBLEM NOT FOUND";
        }

        //Quitamos los problemas del contest
        for(Contest contestAux :problem.get().getListaContestsPertenece()){
            contestAux.getListaProblemas().remove(problem);
        }

        problemRepository.delete(problem.get());
        logger.info("El problema "+problem.get().getNombreEjercicio()+" ha sido eliminado");
        return "OK";
    }

    public String deleteProblem(Problem problem){


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
    public Optional<Problem> getProblem(String idProblem){
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
    private void deleteInNOut(Problem problem){
        for(InNOut inNOut: problem.getEntradaVisible()){
            inNOutRepository.delete(inNOut);
        }
        for(InNOut inNOut: problem.getSalidaVisible()){
            inNOutRepository.delete(inNOut);
        }for(InNOut inNOut: problem.getEntradaOculta()){
            inNOutRepository.delete(inNOut);
        }
        for(InNOut inNOut: problem.getSalidaOculta()){
            inNOutRepository.delete(inNOut);
        }
    }

    private void updateProblemInside(Problem oldProblem, Problem newProblem){
        oldProblem.setNombreEjercicio(newProblem.getNombreEjercicio());
        oldProblem.setEntradaOculta(newProblem.getEntradaOculta());
        oldProblem.setEntradaVisible(newProblem.getEntradaVisible());
        oldProblem.setSalidaOculta(newProblem.getSalidaOculta());
        oldProblem.setSalidaVisible(newProblem.getSalidaVisible());
        //Anyadimos losproblemsvalidator a la lista de viejos
        oldProblem.getOldSubmissionProblemValidators().addAll(oldProblem.getSubmissionProblemValidators());
        oldProblem.setSubmissionProblemValidators(newProblem.getSubmissionProblemValidators());

        //actualizmaos el problema de submissions
        for(Submission submission:newProblem.getSubmissions()){
            submission.setProblema(oldProblem);
        }
        oldProblem.getSubmissions().addAll(newProblem.getSubmissions());

        oldProblem.setEquipoPropietario(newProblem.getEquipoPropietario());

        oldProblem.setValido(newProblem.getValido());
        oldProblem.setTimeout(newProblem.getTimeout());
        oldProblem.setMemoryLimit(newProblem.getMemoryLimit());
        oldProblem.setAutor(newProblem.getAutor());
        oldProblem.setSource(newProblem.getSource());
        oldProblem.setLicense(newProblem.getLicense());
        oldProblem.setRights_owner(newProblem.getRights_owner());
        oldProblem.setDocumento(newProblem.getDocumento());
        oldProblem.setValidation(newProblem.getValidation());
        oldProblem.setValidation_flags(newProblem.getValidation_flags());
        oldProblem.setLimit_time_multiplier(newProblem.getLimit_time_multiplier());
        oldProblem.setLimit_time_safety_margin(newProblem.getLimit_time_safety_margin());
        oldProblem.setLimit_memory(newProblem.getLimit_memory());
        oldProblem.setLimit_output(newProblem.getLimit_output());
        oldProblem.setLimit_code(newProblem.getLimit_code());
        oldProblem.setLimit_compilation_time(newProblem.getLimit_compilation_time());
        oldProblem.setLimit_validation_memory(newProblem.getLimit_validation_memory());
        oldProblem.setLimit_validation_output(newProblem.getLimit_validation_output());
        oldProblem.setColor(newProblem.getColor());
    }

    public ProblemString updateProblemMultipleOptionalParams(String idproblem, Optional<String> nombreProblema, Optional<String> teamId, Optional<byte[]> pdf, Optional<String> timeout){
        ProblemString salida = new ProblemString();

        Optional<Problem> problem = getProblem(idproblem);
        if (problem.isEmpty()){
            salida.setSalida("ERROR PROBLEMID NOT FOUND");
            return salida;
        }


        if(nombreProblema.isPresent()){
            problem.get().setNombreEjercicio(nombreProblema.get());
        }

        if(teamId.isPresent()){
            Optional<Team> team = teamRepository.findTeamById(Long.valueOf(teamId.get()));
            if (team == null){
                salida.setSalida("ERROR TEAMID NOT FOUND");
                return salida;
            }
            problem.get().setEquipoPropietario(team.get());
        }

        if (pdf.isPresent()){
            problem.get().setDocumento(pdf.get());
        }

        if(timeout.isPresent()){
            problem.get().setTimeout(timeout.get());
        }
        problemRepository.save(problem.get());

        salida.setSalida("OK");
        salida.setProblem(problem.get());
        return salida;
    }

    public Page<Problem> getProblemsPage(Pageable pageable){
        return problemRepository.findAll(pageable);
    }
}
