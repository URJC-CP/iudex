package com.example.aplicacion.Controllers.apiControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Pojos.ContestAPI;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.Pojos.SubmissionAPI;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.GET;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class APIController {

    @Autowired
    SubmissionService submissionService;
    @Autowired
    ContestService contestService;
    @Autowired
    ProblemService problemService;




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
