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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/API")
@RestController
public class APIController {

    @Autowired
    SubmissionService submissionService;
    @Autowired
    ContestService contestService;
    @Autowired
    ProblemService problemService;


    @GetMapping("/submissions/all")
    public List<Submission> submissionsAll(){
        return submissionService.getAllSubmissions();
    }

    //CONCURSOS
    @GetMapping("/contests")
    public List<ContestAPI> contests(){
        List<Contest> contestList = contestService.getAllContests();
        List<ContestAPI> contestAPIS = new ArrayList<>();

        for (Contest contest : contestList){
            contestAPIS.add(contest.toContestAPI());
        }
        return contestAPIS;
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
