package com.example.aplicacion.Controllers.apiControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Pojos.ContestAPI;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController

public class APIContestController {


    @Autowired
    SubmissionService submissionService;
    @Autowired
    ContestService contestService;
    @Autowired
    ProblemService problemService;

    //CONCURSOS

    //Get all contest
    @ApiOperation("Return all contests")
    @GetMapping("/API/v1/contest")

    public ResponseEntity<List<ContestAPI>> getAllcontests(){
        List<Contest> contestList = contestService.getAllContests();
        List<ContestAPI> contestAPIS = new ArrayList<>();

        for (Contest contest : contestList){
            contestAPIS.add(contest.toContestAPI());
        }
        return new ResponseEntity<>(contestAPIS, HttpStatus.OK);

    }


    //Get one Contest
    @ApiOperation("Return selected contest with full Problems")
    @GetMapping("/API/v1/contest/{contestId}")
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


    //Crea un concurso
    @ApiOperation("Create a contest")
    @PostMapping("/API/v1/contest")
    public ResponseEntity<ContestAPI> addContest(@RequestParam String contestName, @RequestParam String teamId, @RequestParam Optional<String> descripcion){
        String salida = contestService.creaContest(contestName, teamId, descripcion );
        if (salida.equals("OK")){
            return new  ResponseEntity(HttpStatus.CREATED);
        }
        else {
            return  new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }



    //Delete one Contest
    @ApiOperation("Delete a contest")
    @DeleteMapping("/API/v1/contest/{contestId}")
    public ResponseEntity deleteContest(@PathVariable String contestId){
        String salida = contestService.deleteContest(contestId);
        if (salida.equals("OK")){
            return new  ResponseEntity(HttpStatus.OK);
        }
        else {
            return  new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Update a contest")
    @PutMapping("/API/v1/contest/{contestId}")
    public ResponseEntity<ContestAPI> updateContest(@PathVariable String contestId, @RequestParam String contestName, @RequestParam String teamId, @RequestParam Optional<String> descripccion){

    }


}
