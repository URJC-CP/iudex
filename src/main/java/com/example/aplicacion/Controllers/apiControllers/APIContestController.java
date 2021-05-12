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
@RequestMapping("/API/v1/")
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class APIContestController {

    @Autowired
    ContestService contestService;

    //CONCURSOS

    //Get all contest
    @ApiOperation("Return all contests")
    @GetMapping("contest")
    public ResponseEntity<List<ContestAPI>> getAllcontests() {
        List<Contest> contestList = contestService.getAllContests();
        List<ContestAPI> contestAPIS = new ArrayList<>();

        for (Contest contest : contestList) {
            contestAPIS.add(contest.toContestAPIFull());
        }
        return new ResponseEntity<>(contestAPIS, HttpStatus.OK);
    }

    @ApiOperation("Return Page Contest")
    @GetMapping("contest/page")
    public ResponseEntity<Page<ContestAPI>> getAllContestPage(Pageable pageable) {
        Page<ContestAPI> salida = contestService.getContestPage(pageable).map(Contest::toContestAPI);
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }

    //Get one Contest
    @ApiOperation("Return selected contest with full Problems")
    @GetMapping("contest/{contestId}")
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
    @PostMapping("contest")
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
    @DeleteMapping("contest/{contestId}")
    public ResponseEntity deleteContest(@PathVariable String contestId) {
        String salida = contestService.deleteContest(contestId);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Update a contest")
    @PutMapping("contest/{contestId}")
    public ResponseEntity<ContestAPI> updateContest(@PathVariable String contestId, @RequestParam Optional<String> contestName, @RequestParam Optional<String> teamId, @RequestParam Optional<String> descripcion) {
        ContestString salida = contestService.updateContest(contestId, contestName, teamId, descripcion);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity(salida.getContest().toContestAPI(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Add Problem to Contest")
    @PutMapping("contest/{contestId}/{problemId}")
    public ResponseEntity addProblemToContest(@PathVariable String problemId, @PathVariable String contestId) {
        String salida = contestService.anyadeProblemaContest(contestId, problemId);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Delete a Problem from a Contest")
    @DeleteMapping("contest/{contestId}/{problemId}")
    public ResponseEntity deleteProblemFromContest(@PathVariable String problemId, @PathVariable String contestId) {
        String salida = contestService.deleteProblemFromContest(contestId, problemId);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Add Team to Contest")
    @PutMapping("contest/{contestId}/team/{teamId}")
    public ResponseEntity addTeamToContest(@PathVariable String teamId, @PathVariable String contestId) {
        String salida = contestService.addTeamTocontest(teamId, contestId);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Delete Team From Contest")
    @DeleteMapping("contest/{contestId}/team/{teamId}")
    public ResponseEntity deleteTeamFromContest(@PathVariable String teamId, @PathVariable String contestId) {
        String salida = contestService.deleteTeamFromcontest(contestId, teamId);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Add Language to Contest")
    @PostMapping("contest/{contestId}/language")
    public ResponseEntity addLanguageToContest(@PathVariable String contestId, @RequestParam String language) {
        String salida = contestService.addLanguageToContest(contestId, language);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
    }

    @ApiOperation("Delete Language from contest")
    @DeleteMapping("contest/{contestId}/language/{languageId}")
    public ResponseEntity deleteLanguageFromContest(@PathVariable String contestId, @PathVariable String languageId) {
        String salida = contestService.removeLanguageFromContest(contestId, languageId);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
    }

    @ApiOperation("Set accepted languages of a contest")
    @PostMapping("contest/{contestId}/language/addBulk")
    public ResponseEntity addAcceptedLanguagesToContest(@PathVariable String contestId, @RequestParam(value = "lenguajes") String[] languageList) {
        String salida = contestService.addAcceptedLanguagesToContest(contestId, languageList);
        if (salida.equals("OK")) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
    }
}
