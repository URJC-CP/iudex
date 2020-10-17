package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.Pojos.SubmissionAPI;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class APIProblemController {

    @Autowired
    SubmissionService submissionService;
    @Autowired
    ContestService contestService;
    @Autowired
    ProblemService problemService;



    //PROBLEMS

    //GetProblem
    @GetMapping("/API/v1/problem/{idProblem}")
    public ResponseEntity<ProblemAPI> getProblem(@PathVariable String idProblem){
        Problem problem = problemService.getProblem(idProblem);
        if(problem == null){
            return new ResponseEntity("ERROR PROBLEM NOT FOUND", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(problem.toProblemAPI(), HttpStatus.OK);
    }

    //Get all problems in DB
    @GetMapping("/API/v1/problems")
    public ResponseEntity<List<ProblemAPI>> problems(){
        List<Problem> problems = problemService.getAllProblemas();
        List<ProblemAPI> salida = new ArrayList<>();
        for(Problem problem: problems){
            salida.add(problem.toProblemAPI());
        }
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }



    //Crea problema y devuelve el problema
    @PostMapping("/API/v1/createProblem")
    public ResponseEntity<ProblemAPI> createProblem(@RequestParam MultipartFile file, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String contestId)  {
        ProblemString salida;
        try {
            salida = problemService.addProblemFromZip(file.getOriginalFilename(), file.getInputStream(), teamId, problemaName, contestId);
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
    @GetMapping("/API/v1/getPDF/contest/{idContest}/problema/{idProblem}")
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
    @DeleteMapping("/API/v1/problem/{idProblem}")
    public ResponseEntity deleteProblem(@PathVariable String problemId) {
        String salida = problemService.deleteProblem(problemId);
        if (salida.equals("OK")){
            return new  ResponseEntity(HttpStatus.OK);
        }
        else {
            return  new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping("/API/v1/problem/{idProblem}/Submissions")
    public ResponseEntity<List<SubmissionAPI>> getAllSubmissionsFromProblem(@PathVariable String idProblem){
        Problem problem = problemService.getProblem(idProblem);
        if(problem == null){
            return new ResponseEntity("ERROR PROBLEM NOT FOUND", HttpStatus.NOT_FOUND);
        }

        List<SubmissionAPI> list = problemService.getSubmissionFromProblem(problem).stream().map(submission -> submission.toSubmissionAPI()).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("API/v1/contest/{idContest}/problem/{idProblem}")
    public ResponseEntity <List<SubmissionAPI>> getSubmissionsFromConcurso(@PathVariable String idContest, @PathVariable String idProblem){
        Problem problem = problemService.getProblem(idProblem);
        if(problem == null){
            return new ResponseEntity("ERROR PROBLEM NOT FOUND", HttpStatus.NOT_FOUND);
        }
        Contest contest = contestService.getContest(idContest);
        if(contest == null){
            return new ResponseEntity("ERROR CONTEST NOT FOUND", HttpStatus.NOT_FOUND);
        }
        List<SubmissionAPI> list = problemService.getSubmissionsFromContestFromProblem(contest, problem).stream().map(submission -> submission.toSubmissionAPI()).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    //Delete problem from contest
    @DeleteMapping("/API/v1/contest/{idContest}/problem{idProblem}")
    public ResponseEntity deleteProblemFromContest(@PathVariable String idContest, @PathVariable String idProblem){
        String salida = contestService.deleteProblemFromContest(idContest, idProblem);
        if (salida.equals("OK")){
            return new  ResponseEntity(HttpStatus.OK);
        }
        else {
            return  new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }
    //Get all problems from contest
    @GetMapping("/API/v1/contest/{idContest}/problems")
    public ResponseEntity<List<ProblemAPI>> problemsFromContest(@PathVariable String idContest){
        Contest contest = contestService.getContest(idContest);
        if(contest ==null){
            return new ResponseEntity("CONTEST NOT FOUND",HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(contestService.getProblemsFromConcurso(contest), HttpStatus.OK);
        }
    }

}
