package com.example.aplicacion.Controllers.apiControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Pojos.SubmissionAPI;
import com.example.aplicacion.Pojos.SubmissionStringResult;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class APISubmissionController {

    @Autowired
    SubmissionService submissionService;
    @Autowired
    ContestService contestService;
    @Autowired
    ProblemService problemService;

    @ApiOperation("Get List of submission given problem, contest or both at the same time")
    @GetMapping("/API/v1/submissions")
    public ResponseEntity<List<SubmissionAPI>> getSubmissions(@RequestParam(required = false) Optional<String> contestId, @RequestParam(required = false) Optional<String> problemId) {

        //Si tiene los dos devolvemos lo que corresponde
        if (contestId.isPresent() && problemId.isPresent()) {
            Optional<Contest> contestOptional = contestService.getContest(contestId.get());
            Optional<Problem> problemOptional = problemService.getProblem(problemId.get());
            if (problemOptional.isEmpty() || contestOptional.isEmpty()) {
                return new ResponseEntity("Problem or contest not found", HttpStatus.NOT_FOUND);
            }
            Contest contest = contestOptional.get();
            Problem problem = problemOptional.get();

            if (!contest.getListaProblemas().contains(problem)) {
                return new ResponseEntity("PROBLEM NOT IN THE CONTEST", HttpStatus.NOT_FOUND);
            }
            List<SubmissionAPI> submissionAPIS = new ArrayList<>();
            for (Submission submission : submissionService.getSubmissionFromProblemAndContest(problem, contest)) {
                submissionAPIS.add(submission.toSubmissionAPI());
            }
            return new ResponseEntity(submissionAPIS, HttpStatus.OK);
        } else if (problemId.isPresent()) {
            Optional<Problem> problemOptional = problemService.getProblem(problemId.get());
            if (problemOptional.isEmpty()) {
                return new ResponseEntity("Problem not found", HttpStatus.NOT_FOUND);
            } else {
                Problem problem = problemOptional.get();
                List<SubmissionAPI> submissionAPIS = new ArrayList<>();
                for (Submission submission : submissionService.getSubmissionFromProblem(problem)) {
                    submissionAPIS.add(submission.toSubmissionAPI());
                }
                return new ResponseEntity(submissionAPIS, HttpStatus.OK);
            }
        } else if (contestId.isPresent()) {
            Optional<Contest> contestOptional = contestService.getContest(contestId.get());
            if (contestOptional.isEmpty()) {
                return new ResponseEntity("CONTEST NOT FOUND", HttpStatus.NOT_FOUND);
            } else {
                Contest contest = contestOptional.get();
                List<SubmissionAPI> submissionAPIS = new ArrayList<>();
                for (Submission submission : submissionService.getSubmissionsFromContest(contest)) {
                    submissionAPIS.add(submission.toSubmissionAPI());
                }
                return new ResponseEntity(submissionAPIS, HttpStatus.OK);
            }
        }
        //SI NO CONTIENE NINGUNO devolvemos todo
        else {
            List<SubmissionAPI> submissionAPIS = new ArrayList<>();
            for (Submission submission : submissionService.getAllSubmissions()) {
                submissionAPIS.add(submission.toSubmissionAPI());
            }
            return new ResponseEntity(submissionAPIS, HttpStatus.OK);
        }
    }

    @ApiOperation("Return Page of all submissions")
    @GetMapping("/API/v1/submission/page")
    public ResponseEntity<Page<SubmissionAPI>> getAllSubmisionPage(Pageable pageable) {
        return new ResponseEntity<>(submissionService.getSubmissionsPage(pageable).map(Submission::toSubmissionAPI), HttpStatus.OK);
    }


    @ApiOperation("Get submission with results")
    @GetMapping("/API/v1/submission/{submissionId}")
    public ResponseEntity<SubmissionAPI> getSubmission(@PathVariable String submissionId) {
        Optional<Submission> submissionOptional = submissionService.getSubmission(submissionId);
        if (submissionOptional.isEmpty()) {
            return new ResponseEntity("SUBMISSION NOT FOUND", HttpStatus.NOT_FOUND);
        }
        Submission submission = submissionOptional.get();
        return new ResponseEntity(submission.toSubmissionAPIFull(), HttpStatus.OK);
    }

    @ApiOperation("Create a submission to a problem and contest")
    @PostMapping("/API/v1/submission")
    public ResponseEntity<SubmissionAPI> createSubmission(@RequestParam String problemId, @RequestParam String contestId, @RequestParam MultipartFile codigo, @RequestParam String lenguaje, @RequestParam String teamId) {

        String fileNameaux = codigo.getOriginalFilename();
        String fileName = FilenameUtils.removeExtension(fileNameaux);
        String cod = null;
        try {
            cod = new String(codigo.getBytes());
        } catch (IOException e) {
            return new ResponseEntity("ERROR IN FILE", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        SubmissionStringResult salida = submissionService.creaYejecutaSubmission(cod, problemId, lenguaje, fileName, contestId, teamId);

        if (!salida.getSalida().equals("OK")) {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(salida.getSubmission().toSubmissionAPIFull(), HttpStatus.OK);
    }

    @ApiOperation("Delete API")
    @DeleteMapping("/API/v1/submission/{submissionId}")
    public ResponseEntity deleteSubmission(@PathVariable String submissionId) {
        String salida = submissionService.deleteSubmission(submissionId);
        if (!salida.equals("OK")) {
            return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
