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
    public String index(Model model){
        //Pruebas de rabbit
        model.addAttribute("exercices", problemService.getAllProblemas());
        model.addAttribute("languages", languageService.getNLanguages());
        model.addAttribute("concursos", concursoService.getAllConcursos());
        model.addAttribute("teams", teamService.getAllTeams());
        return "indexOriginal";
    }

    @PostMapping("/answerSubida")
    public String subida(Model model, @RequestParam MultipartFile codigo,  @RequestParam String problemaAsignado, @RequestParam String lenguaje, @RequestParam String teamId, @RequestParam String concursoId) throws IOException {

        String fileNameaux = codigo.getOriginalFilename();
        String fileName = FilenameUtils.removeExtension(fileNameaux);
        String cod = new String(codigo.getBytes());
        //String ent = new String(entrada.getBytes());
        //Crea la submission
        String salida = submissionService.creaSubmission(cod, problemaAsignado, lenguaje, fileName, teamId, concursoId);

        if(!salida.equals("OK")){
            return "ERROR";
        }

        //model.addAttribute("comentario" , "Ha sido creado con el ID: "+salida.getId());
        return "indexOriginal";
    }
    @GetMapping("/scoreboard")
    public String subida (Model model){

        //Cargamos la BBDD de answer en el scoreboard
        Page<Submission> listSubmiss = submissionService.getNSubmissions(10);
        model.addAttribute("submissions", listSubmiss);


        return "indexOriginal";
    }

    @PostMapping("/problemSubida")
    public String subidaProblema(Model model,@RequestParam MultipartFile problema, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String concursoId){
        try {
            problemService.addProblemFromZip(problema.getOriginalFilename(), problema.getInputStream(), teamId, problemaName, concursoId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "indexOriginal";

    }

    @PostMapping("/asignaProblemaAConcurso")
    public String asignaProblemaACcurso(Model model, @RequestParam String problemId, @RequestParam String concursoId){
        concursoService.anyadeProblemaConcurso(concursoId, problemId);
        return "indexOriginal";
    }

    @PostMapping("/creaUsuario")
    public String creaUsuario(Model model, @RequestParam String userNickname, @RequestParam String userMail){
        String salida = userService.crearUsuario(userNickname, userMail);
        if(salida.equals("OK")){
            return "indexOriginal";
        }else {
            return "ERROR algun parametro esta duplicado";
        }
    }
    @PostMapping("/creaConcurso")
    public String creaConcurso(Model model, @RequestParam String concursoName, @RequestParam String teamId){
        concursoService.creaConcurso(concursoName, teamId);

        return "indexOriginal";

    }


    //CONCURSO html
    @PostMapping("/goToConcurso")
    public String goToConcurso(Model model, @RequestParam String concursoId){
        Concurso concurso= concursoService.getConcurso(concursoId);
        if(concurso==null){
            return "ERROR CONCURSO NO ECONTRADO";
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
            return "indexOriginal";
        }
        else {
            return "404";
        }
    }
    @PostMapping("/deleteConcurso")
    public  String deleteConcorso(Model model, @RequestParam String concursoId){
        String salida = concursoService.borraconcurso(concursoId);

        if (salida.equals("OK")){
            return "indexOriginal";
        }
        else {
            return "404";
        }
    }
    @PostMapping("/deleteTeamFromConcurso")
    public String deleteTeamFromConcurso(Model model, @RequestParam String teamId, @RequestParam String concursoId){
        String salida = concursoService.deleteTeamFromconcurso(concursoId, teamId);

        if (salida.equals("OK")){
            return "indexOriginal";
        }
        else {
            return "404";
        }
    }

    @PostMapping("/addProblemToConcurso")
    public String addProblemToConcurso(Model model, @RequestParam String problemId, @RequestParam String concursoId){
        String salida = concursoService.anyadeProblemaConcurso(concursoId, problemId);
        if (salida.equals("OK")){
            return "indexOriginal";
        }
        else {
            return "404";
        }
    }
}
