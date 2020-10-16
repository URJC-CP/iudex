package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Pojos.ContestAPI;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/API/v1")
@RestController
public class APIController {

    @Autowired
    SubmissionService submissionService;
    @Autowired
    ContestService contestService;
    @Autowired
    ProblemService problemService;


    //CONCURSOS

    //Get all concursos
    @GetMapping("/contests")
    public ResponseEntity<List<ContestAPI>> getAllcontests(){
        List<Contest> contestList = contestService.getAllContests();
        List<ContestAPI> contestAPIS = new ArrayList<>();

        for (Contest contest : contestList){
            contestAPIS.add(contest.toContestAPI());
        }
        return new ResponseEntity<>(contestAPIS, HttpStatus.OK);
    }

    //Get page contest
    //@GetMapping("/contests/page")



    //Get one Contest
    @GetMapping("/contest/{contestId}")
    public ResponseEntity<ContestAPI> getContest(@PathVariable String contestId){

        ContestAPI contestAPI = new ContestAPI();
        Contest contest = contestService.getContest(contestId);
        if(contest ==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        contestAPI=contest.toContestAPI();

        ResponseEntity<ContestAPI> responseEntity = new ResponseEntity<>(contestAPI, HttpStatus.OK);
        return responseEntity;
    }

    //Delete one Contest
    @DeleteMapping("/contest/{contestId}")
    public ResponseEntity deleteContest(@PathVariable String contestId){
        String salida = contestService.deleteContest(contestId);
        if (salida.equals("OK")){
          return new  ResponseEntity(HttpStatus.OK);
        }
        else {
            return  new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }

    }

    //Crea un concurso
    @PostMapping("/addContest")
    public ResponseEntity addContest(@RequestParam String contestId, @RequestParam String teamId){
        String salida = contestService.creaContest(contestId, teamId);
        if (salida.equals("OK")){
            return new  ResponseEntity(HttpStatus.CREATED);
        }
        else {
            return  new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }





    //PROBLEMS

    //Get all problems in DB
    @GetMapping("/problems")
    public ResponseEntity<List<ProblemAPI>> problems(){
        List<Problem> problems = problemService.getAllProblemas();
        List<ProblemAPI> salida = new ArrayList<>();
        for(Problem problem: problems){
            salida.add(problem.toProblemAPI());
        }
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }

    //Get all problems from contest
    @GetMapping("/contest/{idContest}/problems")
    public ResponseEntity<List<ProblemAPI>> problemsFromContest(@PathVariable String idContest){
        Contest contest = contestService.getContest(idContest);
        if(contest ==null){
            return new ResponseEntity("CONTEST NOT FOUND",HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(contestService.getProblemsFromConcurso(contest), HttpStatus.OK);
        }
    }

    //Crea problema y devuelve el problema
    @PostMapping("/createProblem")
    public ResponseEntity<ProblemAPI> createProblem(@RequestParam MultipartFile problema, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String contestId)  {
        ProblemString salida;
        try {
             salida = problemService.addProblemFromZip(problema.getOriginalFilename(), problema.getInputStream(), teamId, problemaName, contestId);
        } catch (Exception e) {
            return new ResponseEntity("ERROR IN FILE", HttpStatus.NOT_ACCEPTABLE);
        }
        if(salida.getSalida().equals("OK")){
            return new ResponseEntity<>(salida.getProblem().toProblemAPI(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }

    //Devuelve el pdf del problema
    //Controller que devuelve en un HTTP el pdf del problema pedido
    @GetMapping("getPDF/contest/{idContest}/problema/{idProblem}")
    public ResponseEntity<byte[]> goToProblem2(Model model, @PathVariable String idContest, @PathVariable String idProblem){
        Problem problem = problemService.getProblem(idProblem);
        Contest contest = contestService.getContest(idContest);
        if(contest ==null){
            return  new ResponseEntity("ERROR CONCURSO NO ENCONTRADO", HttpStatus.NOT_FOUND);
        }
        if(problem==null){
            return  new ResponseEntity("ERROR PROBLEMA NO ECONTRADO", HttpStatus.NOT_FOUND);
        }
        if(!contest.getListaProblemas().contains(problem)){
            return  new ResponseEntity("ERROR PROBLEMA NO PERTENCE A CONCURSO", HttpStatus.NOT_FOUND);
        }

        byte[] contents = problem.getDocumento();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = problem.getNombreEjercicio()+".pdf";
        //headers.setContentDispositionFormData(filename, filename);
        headers.setContentDisposition(ContentDisposition.builder("inline").build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }

    //DeleteProblem
    @DeleteMapping("/problem/{idProblem}")
    public ResponseEntity deleteProblem(@PathVariable String problemId) {
        String salida = problemService.deleteProblem(problemId);
        if (salida.equals("OK")){
            return new  ResponseEntity(HttpStatus.OK);
        }
        else {
            return  new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }

        
    }








    }
