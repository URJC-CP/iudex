package com.example.aplicacion.controllers.api_controllers;

import com.example.aplicacion.entities.Contest;
import com.example.aplicacion.pojos.ContestAPI;
import com.example.aplicacion.pojos.ContestString;
import com.example.aplicacion.pojos.TeamScore;
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

import static com.example.aplicacion.utils.Sanitizer.sanitize;

@RestController
@RequestMapping("/API/v1/")
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class APIContestController {

    @Autowired
    ContestService contestService;

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

    @ApiOperation("Return selected contest with full Problems")
    @GetMapping("contest/{contestId}")
    public ResponseEntity<ContestAPI> getContest(@PathVariable String contestId) {
        contestId = sanitize(contestId);

        Optional<Contest> contestOptional = contestService.getContestById(contestId);
        if (contestOptional.isPresent()) {
            Contest contest = contestOptional.get();
            ContestAPI contestAPI = contest.toContestAPI();
            return new ResponseEntity<>(contestAPI, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Create a contest")
    @PostMapping("contest")
    public ResponseEntity<ContestAPI> addContest(@RequestParam String contestName, @RequestParam String teamId, @RequestParam Optional<String> descripcion, @RequestParam long startTimestamp, @RequestParam long endTimestamp) {
        contestName = sanitize(contestName);
        teamId = sanitize(teamId);
        descripcion = sanitize(descripcion);

        ContestString salida = contestService.creaContest(contestName, teamId, descripcion, startTimestamp, endTimestamp);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getContest().toContestAPI(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Delete a contest")
    @DeleteMapping("contest/{contestId}")
    public ResponseEntity<String> deleteContest(@PathVariable String contestId) {
        contestId = sanitize(contestId);
        String salida = contestService.deleteContest(contestId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Update a contest")
    @PutMapping("contest/{contestId}")
    public ResponseEntity<ContestAPI> updateContest(@PathVariable String contestId, @RequestParam Optional<String> contestName, @RequestParam Optional<String> teamId, @RequestParam Optional<String> descripcion, @RequestParam Optional<Long> startTimestamp, @RequestParam Optional<Long> endTimestamp) {
        contestId = sanitize(contestId);
        contestName = sanitize(contestName);
        teamId = sanitize(teamId);
        descripcion = sanitize(descripcion);

        ContestString salida = contestService.updateContest(contestId, contestName, teamId, descripcion, startTimestamp, endTimestamp);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getContest().toContestAPI(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Add Problem to Contest")
    @PutMapping("contest/{contestId}/{problemId}")
    public ResponseEntity<String> addProblemToContest(@PathVariable String problemId, @PathVariable String contestId) {
        problemId = sanitize(problemId);
        contestId = sanitize(contestId);

        String salida = contestService.anyadeProblemaContest(contestId, problemId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Delete a Problem from a Contest")
    @DeleteMapping("contest/{contestId}/{problemId}")
    public ResponseEntity<String> deleteProblemFromContest(@PathVariable String problemId, @PathVariable String contestId) {
        problemId = sanitize(problemId);
        contestId = sanitize(contestId);

        String salida = contestService.deleteProblemFromContest(contestId, problemId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Add Team to Contest")
    @PutMapping("contest/{contestId}/team/{teamId}")
    public ResponseEntity<String> addTeamToContest(@PathVariable String contestId, @PathVariable String teamId) {
        contestId = sanitize(contestId);
        teamId = sanitize(teamId);

        String salida = contestService.addTeamToContest(contestId, teamId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Bulk add Team to Contest")
    @PutMapping("contest/{contestId}/team/addBulk")
    public ResponseEntity<String> addTeamToContest(@PathVariable String contestId, @RequestParam String[] teamList) {
        contestId = sanitize(contestId);

        String salida = contestService.addTeamToContest(contestId, teamList);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Delete Team From Contest")
    @DeleteMapping("contest/{contestId}/team/{teamId}")
    public ResponseEntity<String> deleteTeamFromContest(@PathVariable String contestId, @PathVariable String teamId) {
        contestId = sanitize(contestId);
        teamId = sanitize(teamId);

        String salida = contestService.deleteTeamFromContest(contestId, teamId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Bulk delete Team From Contest")
    @DeleteMapping("contest/{contestId}/team/removeBulk")
    public ResponseEntity<String> deleteTeamFromContest(@PathVariable String contestId, @RequestParam String[] teamList) {
        contestId = sanitize(contestId);

        String salida = contestService.deleteTeamFromContest(contestId, teamList);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Add Language to Contest")
    @PostMapping("contest/{contestId}/language")
    public ResponseEntity<String> addLanguageToContest(@PathVariable String contestId, @RequestParam String language) {
        contestId = sanitize(contestId);
        language = sanitize(language);

        String salida = contestService.addLanguageToContest(contestId, language);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
    }

    @ApiOperation("Delete Language from contest")
    @DeleteMapping("contest/{contestId}/language/{languageId}")
    public ResponseEntity<String> deleteLanguageFromContest(@PathVariable String contestId, @PathVariable String languageId) {
        contestId = sanitize(contestId);
        languageId = sanitize(languageId);

        String salida = contestService.removeLanguageFromContest(contestId, languageId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
    }

    @ApiOperation("Set accepted languages of a contest")
    @PostMapping("contest/{contestId}/language/addBulk")
    public ResponseEntity<String> addAcceptedLanguagesToContest(@PathVariable String contestId, @RequestParam String[] languageList) {
        contestId = sanitize(contestId);

        String salida = contestService.addAcceptedLanguagesToContest(contestId, languageList);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
    }

    @ApiOperation("Get scores of a contest")
    @GetMapping("contest/{contestId}/scoreboard")
    public ResponseEntity<List<TeamScore>> getScores(@PathVariable String contestId) {
        contestId = sanitize(contestId);
        try {
            List<TeamScore> scores = contestService.getScore(contestId);
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
