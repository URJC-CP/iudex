package com.example.aplicacion.Controllers.apiControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Pojos.SubmissionAPI;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class APISubmissionController {

    @Autowired
    SubmissionService submissionService;
    @Autowired
    ContestService contestService;
    @Autowired
    ProblemService problemService;


    @ApiOperation("Get List of submission given problem, contest or both at the same time")
    @GetMapping("/API/v1/submissions")
    public ResponseEntity<List<SubmissionAPI>> getSubmissions(@RequestParam(required = false) Optional<String> contestId, @RequestParam(required = false) Optional<String> problemId){

        //Si tiene los dos devolvemos lo que corresponde
        if(contestId.isPresent()&&problemId.isPresent()){
            Contest contest = contestService.getContest(contestId.get());
            Problem problem = problemService.getProblem(problemId.get());
            if (problem == null || contest == null){
                return  new ResponseEntity("Problem or contest not found", HttpStatus.NOT_FOUND);
            }
            if (!contest.getListaProblemas().contains(problem)){
                return new ResponseEntity("PROBLEM NOT IN THE CONTEST", HttpStatus.NOT_FOUND);
            }
            List<SubmissionAPI> submissionAPIS = new ArrayList<>();
            for (Submission submission: submissionService.getSubmissionFromProblemAndContest(problem,contest)){
                submissionAPIS.add(submission.toSubmissionAPI());
            }
            return new ResponseEntity(submissionAPIS, HttpStatus.OK);
        }
        else if(problemId.isPresent()){
            Problem problem = problemService.getProblem(problemId.get());
            if (problem == null){
                return  new ResponseEntity("Problem not found",HttpStatus.NOT_FOUND);
            }
            else {
                List<SubmissionAPI> submissionAPIS = new ArrayList<>();
                for (Submission submission : submissionService.getSubmissionFromProblem(problem)){
                    submissionAPIS.add(submission.toSubmissionAPI());
                }
                return new ResponseEntity(submissionAPIS, HttpStatus.OK);
            }
        }
        else if (contestId.isPresent()){
            Contest contest = contestService.getContest(contestId.get());
            if (contest == null) {
                return  new ResponseEntity("CONTEST NOT FOUND", HttpStatus.NOT_FOUND);
            }
            else {
                List<SubmissionAPI> submissionAPIS = new ArrayList<>();
                for (Submission submission : submissionService.getSubmissionsFromContest(contest)){
                    submissionAPIS.add(submission.toSubmissionAPI());
                }
                return new ResponseEntity(submissionAPIS, HttpStatus.OK);
            }
        }
        //SI NO CONTIENE NINGUNO devolvemos todo
        else {
            List<SubmissionAPI> submissionAPIS = new ArrayList<>();
            for (Submission submission: submissionService.getAllSubmissions()){
                submissionAPIS.add(submission.toSubmissionAPI());
            }
            return new ResponseEntity(submissionAPIS, HttpStatus.OK);
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

}
