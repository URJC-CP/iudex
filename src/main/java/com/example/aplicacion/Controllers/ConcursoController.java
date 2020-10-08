package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.Concurso;
import com.example.aplicacion.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class ConcursoController {
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


    //CONCURSO html
    @GetMapping("/goToConcurso")
    public String goToConcurso(Model model, @RequestParam String concursoId){
        Concurso concurso= concursoService.getConcurso(concursoId);
        if(concurso==null){
            model.addAttribute("error", "ERROR CONCURSO NO ECONTRADO");
            return "errorConocido";
        }
        model.addAttribute("concurso",concurso);
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("problems", problemService.getAllProblemas());

        return "concurso";
    }
    @PostMapping("/addUserToConcurso")
    public String addUserToConcuro(Model model, @RequestParam String teamId, @RequestParam String concursoId){

        String salida = concursoService.addTeamToconcurso(teamId, concursoId);

        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
    @PostMapping("/deleteConcurso")
    public  String deleteConcorso(Model model, @RequestParam String concursoId){
        String salida = concursoService.deleteConcurso(concursoId);

        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {

            return "404";
        }
    }
    @PostMapping("/deleteTeamFromConcurso")
    public String deleteTeamFromConcurso(Model model, @RequestParam String teamId, @RequestParam String concursoId){
        String salida = concursoService.deleteTeamFromconcurso(concursoId, teamId);

        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/addProblemToConcurso")
    public String addProblemToConcurso(Model model, @RequestParam String problemId, @RequestParam String concursoId){
        String salida = concursoService.anyadeProblemaConcurso(concursoId, problemId);
        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
    @PostMapping("/deleteProblemFromConcurso")
    public String deleteProblemFromConcurso(Model model, @RequestParam String problemId, @RequestParam String concursoId){
        String salida = concursoService.deleteProblemFromConcurso(concursoId, problemId);

        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
}
