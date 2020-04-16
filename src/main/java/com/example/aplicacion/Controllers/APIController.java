package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/API")
@RestController
public class APIController {

    @Autowired
    SubmissionService submissionService;


    @GetMapping("/")
    public String index(){
        return "HOLA MUNDO";
    }

    @GetMapping("/submissions/all")
    public List<Submission> submissionsAll(){
        return submissionService.getAllSubmissions();
    }

    @PostMapping("/addSubmission")
    public String addsubmissions(@RequestParam String codigo,@RequestParam String filename,  @RequestParam String lenguaje, @RequestParam String problema){
        submissionService.crearPeticion(codigo, problema, lenguaje, filename);

        return "Submission subida";
    }






}
