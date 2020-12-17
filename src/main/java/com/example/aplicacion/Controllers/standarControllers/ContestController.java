package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller

public class ContestController {
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;


    //CONCURSO html
    @GetMapping("/goToContest")
    public String goToContest(Model model, @RequestParam String contestId){
        Optional<Contest> contest = contestService.getContest(contestId);
        if(contest.isEmpty()){
            model.addAttribute("error", "ERROR CONCURSO NO ECONTRADO");
            return "errorConocido";
        }
        model.addAttribute("contest", contest.get());
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("problems", problemService.getAllProblemas());

        return "contest";
    }

    @PostMapping("/deleteContest")
    public  String deleteConcorso(Model model, @RequestParam String contestId){
        String salida = contestService.deleteContest(contestId);

        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {

            return "404";
        }
    }
    @PostMapping("/addUserToContest")
    public String addUserToConcuro(Model model, @RequestParam String teamId, @RequestParam String contestId){

        String salida = contestService.addTeamTocontest(teamId, contestId);

        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
    @PostMapping("/deleteTeamFromContest")
    public String deleteTeamFromContest(Model model, @RequestParam String teamId, @RequestParam String contestId){
        String salida = contestService.deleteTeamFromcontest(contestId, teamId);

        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/addProblemToContest")
    public String addProblemToContest(Model model, @RequestParam String problemId, @RequestParam String contestId){
        String salida = contestService.anyadeProblemaContest(contestId, problemId);
        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
    @PostMapping("/deleteProblemFromContest")
    public String deleteProblemFromContest(Model model, @RequestParam String problemId, @RequestParam String contestId){
        String salida = contestService.deleteProblemFromContest(contestId, problemId);

        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
}
