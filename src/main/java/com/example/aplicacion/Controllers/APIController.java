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
    public List<ContestAPI> getAllcontests(){
        List<Contest> contestList = contestService.getAllContests();
        List<ContestAPI> contestAPIS = new ArrayList<>();

        for (Contest contest : contestList){
            contestAPIS.add(contest.toContestAPI());
        }
        return contestAPIS;
    }

    //Get one Contest
    @GetMapping("/contest/{contestId}")
    public ContestAPI getContest(@PathVariable String contestId){
        ContestAPI contestAPI = new ContestAPI();
        Contest contest = contestService.getContest(contestId);
        if(contest ==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CONTEST NOT FOUND");
        }
        contestAPI=contest.toContestAPI();

        return contestAPI;
    }



    //PROBLEMS
    @GetMapping("/problems")
    public List<ProblemAPI> problems(){
        List<Problem> problems = problemService.getAllProblemas();
        List<ProblemAPI> salida = new ArrayList<>();
        for(Problem problem: problems){
            salida.add(problem.toProblemAPI());
        }
        return salida;
    }






}
