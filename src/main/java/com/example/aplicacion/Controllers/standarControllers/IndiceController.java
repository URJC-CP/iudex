package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Pojos.SubmissionStringResult;
import com.example.aplicacion.services.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Optional;

@Controller
public class IndiceController {

    private final RabbitTemplate rabbitTemplate;
    Logger logger = LoggerFactory.getLogger(RabbitTemplate.class);
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

    //Inicio del rabbittemplate
    public IndiceController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/")
    public ModelAndView index() {
        logger.debug("Get request received for Rabbit");
        ModelAndView model = new ModelAndView();
        //Pruebas de rabbit
        model.getModel().put("exercices", problemService.getAllProblemas());
        model.getModel().put("languages", languageService.getNLanguages());
        model.getModel().put("contests", contestService.getAllContests());
        model.getModel().put("teams", teamService.getAllTeams());
        model.setViewName("indexOriginal");
        //return new RedirectView()
        logger.debug("Showing Rabbit's main page");
        return model;
    }

    @PostMapping("/answerSubida")
    public String subida(Model model, @RequestParam MultipartFile codigo, @RequestParam String problemaAsignado, @RequestParam String lenguaje, @RequestParam String teamId, @RequestParam String contestId) throws IOException {
        logger.debug("Answer has been submitted\nProblem: " + problemaAsignado + ", Language: " + lenguaje
                + "\nTeam: " + teamId + ", Contest: " + contestId);
        String fileNameaux = codigo.getOriginalFilename();
        String fileName = FilenameUtils.removeExtension(fileNameaux);
        String cod = new String(codigo.getBytes());
        //String ent = new String(entrada.getBytes());

        logger.debug("Running Submission...\nProblem: " + problemaAsignado + ", Language: " + lenguaje
                + "\nTeam: " + teamId + ", Contest: " + contestId);
        //Crea la submission
        SubmissionStringResult salida = submissionService.creaYejecutaSubmission(cod, problemaAsignado, lenguaje, fileName, contestId, teamId);
        logger.debug("Submission " + salida.getSubmission().getId() + " finished running with " + salida.getSalida());

        if (salida.equals("OK")) {
            return "redirect:/";
        } else {
            model.addAttribute("error", salida.getSalida());
            return "errorConocido";
        }
    }

    @GetMapping("/scoreboard")
    public String subida(Model model) {

        //Cargamos la BBDD de answer en el scoreboard
        Page<Submission> listSubmiss = submissionService.getNSubmissions(10);
        model.addAttribute("submissions", listSubmiss);


        return "redirect:/";
    }

    @PostMapping("/asignaProblemaAContest")
    public String asignaProblemaACcurso(Model model, @RequestParam String problemId, @RequestParam String contestId) {
        logger.debug("Adding problem " + problemId + " to contest " + contestId);
        String salida = contestService.anyadeProblemaContest(contestId, problemId);

        if (salida.equals("OK")) {
            logger.debug("Add problem " + problemId + " to contest " + contestId + " success");
        } else {
            logger.error("Add problem " + problemId + " to contest " + contestId + " failed with " + salida);
        }
        return "indexOriginal";
    }

    @PostMapping("/creaUsuario")
    public String creaUsuario(Model model, @RequestParam String userNickname, @RequestParam String userMail) {
        logger.debug("Create user with " + userNickname + " and " + userMail + " request received");
        String salida = userService.crearUsuario(userNickname, userMail).getSalida();

        if (salida.equals("OK")) {
            logger.debug("Create user " + userNickname + " and " + userMail + " success");
            return "redirect:/";
        } else {
            logger.error("Create user " + userNickname + " and " + userMail + " failed with " + salida);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/creaContest")
    public String creaContest(Model model, @RequestParam String contestName, @RequestParam String teamId, @RequestParam Optional<String> descripcion) {
        logger.debug("Create contest " + contestName + " request received for team " + teamId);
        String salida = contestService.creaContest(contestName, teamId, descripcion).getSalida();

        if (salida.equals("OK")) {
            logger.debug("Create contest " + contestName + " success for team " + teamId);
        } else {
            logger.error("Create contest " + contestName + " for team " + teamId + " failed with " + salida);
        }
        return "redirect:/";
    }

}
