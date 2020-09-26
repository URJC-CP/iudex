package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.Concurso;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProblemaController {

    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private ConcursoService concursoService;
    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;


    @GetMapping("/concurso/{idConcurso}/problema/{idProblem}")
    public String goToProblem(Model model, @PathVariable String idConcurso, @PathVariable String idProblem){
        Problem problem = problemService.getProblem(idProblem);
        Concurso concurso = concursoService.getConcurso(idConcurso);
        if(concurso==null){
            model.addAttribute("error", "ERROR CONCURSO NO ECONTRADO");
            return "errorConocido";
        }
        if(problem==null){
            model.addAttribute("error", "ERROR PROBLEMA NO ECONTRADO");
            return "errorConocido";
        }

        
        return "problem";
    }
}
