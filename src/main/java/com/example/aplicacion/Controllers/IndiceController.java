package com.example.aplicacion.Controllers;


import com.example.aplicacion.Entities.Concurso;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.services.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;


@Controller
public class IndiceController {

    private  final RabbitTemplate rabbitTemplate;


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

    //Inicio del rabbittemplate
    public IndiceController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @GetMapping("/")
    public ModelAndView index(){
        ModelAndView model = new ModelAndView();
        //Pruebas de rabbit
        model.getModel().put("exercices", problemService.getAllProblemas());
        model.getModel().put("languages", languageService.getNLanguages());
        model.getModel().put("concursos", concursoService.getAllConcursos());
        model.getModel().put("teams", teamService.getAllTeams());
        model.setViewName("indexOriginal");
        //return new RedirectView()
        return model;
    }

    @PostMapping("/answerSubida")
    public String subida(Model model, @RequestParam MultipartFile codigo,  @RequestParam String problemaAsignado, @RequestParam String lenguaje, @RequestParam String teamId, @RequestParam String concursoId) throws IOException {

        String fileNameaux = codigo.getOriginalFilename();
        String fileName = FilenameUtils.removeExtension(fileNameaux);
        String cod = new String(codigo.getBytes());
        //String ent = new String(entrada.getBytes());
        //Crea la submission
        String salida = submissionService.creaSubmission(cod, problemaAsignado, lenguaje, fileName, teamId, concursoId);

        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
    @GetMapping("/scoreboard")
    public String subida (Model model){

        //Cargamos la BBDD de answer en el scoreboard
        Page<Submission> listSubmiss = submissionService.getNSubmissions(10);
        model.addAttribute("submissions", listSubmiss);


        return "redirect:/";
    }



    @PostMapping("/asignaProblemaAConcurso")
    public String asignaProblemaACcurso(Model model, @RequestParam String problemId, @RequestParam String concursoId){
        concursoService.anyadeProblemaConcurso(concursoId, problemId);
        return "indexOriginal";
    }

    @PostMapping("/creaUsuario")
    public String creaUsuario(Model model, @RequestParam String userNickname, @RequestParam String userMail){
        String salida = userService.crearUsuario(userNickname, userMail);
        if (salida.equals("OK")){
            return "redirect:/";
        }
        else {
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
    @PostMapping("/creaConcurso")
    public String creaConcurso(Model model, @RequestParam String concursoName, @RequestParam String teamId){
        concursoService.creaConcurso(concursoName, teamId);

        return "redirect:/";

    }




}
