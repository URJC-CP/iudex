package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.InNOut;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Pojos.ProblemEntradaSalidaVisiblesHTML;
import com.example.aplicacion.Pojos.ProblemStringResult;
import com.example.aplicacion.Repository.ContestRepository;
import com.example.aplicacion.Repository.InNOutRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.TeamRepository;
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
    Logger logger = LoggerFactory.getLogger(ProblemService.class);


    public void addProblem(String nombre, List<InNOut> entrada, List<InNOut>  salidaCorrecta, List<InNOut> codigoCorrecto, List<InNOut>  entradaVisible, List<InNOut>  salidaVisible ){
        problemRepository.save(new Problem(nombre, entrada, salidaCorrecta, codigoCorrecto, entradaVisible, salidaVisible));
    }


    public String addProblemFromZip(String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        Problem problem = new Problem();
        ProblemStringResult problemStringResult = new ProblemStringResult();

        Team team =teamRepository.findTeamById(Long.valueOf(teamId));
        if (team == null) {
            return "TEAM NOT FOUND";
        }
        problem.setEquipoPropietario(team);
        Contest contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if(contest ==null){
            return "CONCURSO NOT FOUND";
        }
        //Si el usuario introduce un nombre lo metemos a cholon
        if(!nombreProblema.equals("")){
            problemStringResult = zipHandlerService.generateProblemFromZIP(problem, nombreFichero, inputStream, idcontest, teamId);
            problem = problemStringResult.getProblem();
            problem.setNombreEjercicio(nombreProblema);
        }
        //Si no mete nombre cogera el que tenga en el .yml. Si no tiene en el yml cogera el nombre del archivo como nombre del problema.
        else {
            problemStringResult = zipHandlerService.generateProblemFromZIP(problem, nombreFichero, inputStream, idcontest, teamId);
            problem = problemStringResult.getProblem();
        }

        //Verificamos si hubiera dado fallo el problema al guardarse
        //SI FALLA NO SE GUARDA EL PROBLEMA
        if(!(problemStringResult.getSalida()==null)){
            //problemRepository.deleteById(problem.getId());
            return problemStringResult.getSalida();
        }

        contest.addProblem(problem);
        problemRepository.save(problem);

        contestRepository.save(contest);

        problemValidatorService.validateProblem(problem);
        return "OK";

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
}
