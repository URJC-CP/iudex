package com.example.aplicacion.Controllers.apiControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Pojos.ContestAPI;
import com.example.aplicacion.Pojos.ContestString;
import com.example.aplicacion.services.ContestService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class APIContestController {

    @Autowired
    ContestService contestService;

    //CONCURSOS

    //Get all contest
    @ApiOperation("Return all contests")
    @GetMapping("/API/v1/contest")
    public ResponseEntity<List<ContestAPI>> getAllcontests() {
        List<Contest> contestList = contestService.getAllContests();
        List<ContestAPI> contestAPIS = new ArrayList<>();

        for (Contest contest : contestList) {
            contestAPIS.add(contest.toContestAPIFull());
        }
        return new ResponseEntity<>(contestAPIS, HttpStatus.OK);
    }

    @ApiOperation("Return Page Contest")
    @GetMapping("/API/v1/contest/page")
    public ResponseEntity<Page<ContestAPI>> getAllContestPage(Pageable pageable) {
        Page<ContestAPI> salida = contestService.getContestPage(pageable).map(Contest::toContestAPI);
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }

    //Get one Contest
    @ApiOperation("Return selected contest with full Problems")
    @GetMapping("/API/v1/contest/{contestId}")
    public ResponseEntity<ContestAPI> getContest(@PathVariable String contestId) {

        ContestAPI contestAPI = new ContestAPI();
        Optional<Contest> contestOptional = contestService.getContest(contestId);
        if (contestOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Contest contest = contestOptional.get();
        contestAPI = contest.toContestAPI();

        ResponseEntity<ContestAPI> responseEntity = new ResponseEntity<>(contestAPI, HttpStatus.OK);
        return responseEntity;
    }

    //Crea un concurso
    @ApiOperation("Create a contest")
    @PostMapping("/API/v1/contest")
    public ResponseEntity<ContestAPI> addContest(@RequestParam String contestName, @RequestParam String teamId, @RequestParam Optional<String> descripcion) {
        ContestString salida = contestService.creaContest(contestName, teamId, descripcion);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity(salida.getContest().toContestAPI(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }

    //Delete one Contest
    @ApiOperation("Delete a contest")
    @DeleteMapping("/API/v1/contest/{contestId}")
    public ResponseEntity deleteContest(@PathVariable String contestId) {
        String salida = contestService.deleteContest(contestId);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Update a contest")
    @PutMapping("/API/v1/contest/{contestId}")
    public ResponseEntity<ContestAPI> updateContest(@PathVariable String contestId, @RequestParam Optional<String> contestName, @RequestParam Optional<String> teamId, @RequestParam Optional<String> descripcion) {
        ContestString salida = contestService.updateContest(contestId, contestName, teamId, descripcion);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity(salida.getContest().toContestAPI(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Add Problem to Contest")
    @PostMapping("/API/v1/contest/{contestId}/{problemId}")
    public ResponseEntity addProblemToContest(@PathVariable String problemId, @PathVariable String contestId) {
        String salida = contestService.anyadeProblemaContest(contestId, problemId);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Delete a Problem from a Contest")
    @DeleteMapping("/contest/{contestId}/{problemId}")
    public ResponseEntity deleteProblemFromContest(@PathVariable String problemId, @PathVariable String contestId) {
        String salida = contestService.deleteProblemFromContest(contestId, problemId);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }
}
