package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Pojos.ContestAPI;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @GetMapping("/problems")
    public ResponseEntity<List<ProblemAPI>> problems(){
        List<Problem> problems = problemService.getAllProblemas();
        List<ProblemAPI> salida = new ArrayList<>();
        for(Problem problem: problems){
            salida.add(problem.toProblemAPI());
        }
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }






}
