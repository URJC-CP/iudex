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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

import static com.example.aplicacion.utils.Sanitizer.sanitize;

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
        logger.debug("Get main page");
        ModelAndView model = new ModelAndView();
        //Pruebas de rabbit
        model.getModel().put("exercices", problemService.getAllProblemas());
        model.getModel().put("languages", languageService.getNLanguages());
        model.getModel().put("contests", contestService.getAllContests());
        model.getModel().put("teams", teamService.getAllTeams());
        model.setViewName("indexOriginal");
        //return new RedirectView()
        logger.debug("Show main page");
        return model;
    }

    @PostMapping("/answerSubida")
    public String subida(Model model, @RequestParam MultipartFile codigo, @RequestParam String problemaAsignado, @RequestParam String lenguaje, @RequestParam String teamId, @RequestParam String contestId) throws IOException {
        problemaAsignado = sanitize(problemaAsignado);
        lenguaje = sanitize(lenguaje);
        teamId = sanitize(teamId);
        contestId = sanitize(contestId);

        logger.debug("Submit {0} code to problem {1}", lenguaje, problemaAsignado);
        String fileName = FilenameUtils.removeExtension(codigo.getOriginalFilename());
        String cod = new String(codigo.getBytes());

        logger.debug("Running submission");
        //Crea la submission
        SubmissionStringResult salida = submissionService.creaYejecutaSubmission(cod, problemaAsignado, lenguaje, fileName, contestId, teamId);

        if (salida.equals("OK")) {
            logger.debug("Run submission {1} success", lenguaje, salida.getSubmission().getId());
            return "redirect:/";
        } else {
            logger.debug("Run submission failed with {0}", salida.getSalida());
            model.addAttribute("error", salida.getSalida());
            return "errorConocido";
        }
    }

    @GetMapping("/scoreboard")
    public String subida(Model model) {
        logger.debug("Get scoreboard");
        //Cargamos la BBDD de answer en el scoreboard
        Page<Submission> listSubmiss = submissionService.getNSubmissions(10);
        model.addAttribute("submissions", listSubmiss);
        logger.debug("Show submission list");

        return "redirect:/";
    }

    @PostMapping("/asignaProblemaAContest")
    public String asignaProblemaACcurso(Model model, @RequestParam String problemId, @RequestParam String contestId) {
        problemId = sanitize(problemId);
        contestId = sanitize(contestId);

        logger.debug("Add problem {0} to contest {1}", problemId, contestId);
        String salida = contestService.anyadeProblemaContest(contestId, problemId);

        if (salida.equals("OK")) {
            logger.debug("Add problem {0} to contest {1} success", problemId, contestId);
        } else {
            logger.debug("Add problem {0} to contest {1} failed with {2}", problemId, contestId, salida);
        }
        return "indexOriginal";
    }

    @PostMapping("/creaUsuario")
    public String creaUsuario(Model model, @RequestParam String userNickname, @RequestParam String userMail) {
        userNickname = sanitize(userNickname);
        userMail = sanitize(userMail);

        logger.debug("Create user {0}", userNickname);
        String salida = userService.crearUsuario(userNickname, userMail).getSalida();

        if (salida.equals("OK")) {
            logger.debug("Create user {0} success", userNickname);
            return "redirect:/";
        } else {
            logger.error("Create user {0} failed with {1} ", userNickname, salida);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/creaContest")
    public String creaContest(Model model, @RequestParam String contestName, @RequestParam String teamId, @RequestParam Optional<String> descripcion) {
        contestName = sanitize(contestName);
        teamId = sanitize(teamId);
        descripcion = sanitize(descripcion);

        logger.debug("Create contest {0}", contestName);

        long startDateTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        long endDateTime = LocalDateTime.now().plusDays(1).atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();

        String salida = contestService.creaContest(contestName, teamId, descripcion, startDateTime, endDateTime).getSalida();

        if (salida.equals("OK")) {
            logger.debug("Create contest {0} success", contestName);
        } else {
            logger.error("Create contest {0} failed with {1} ", contestName, salida);
        }
        return "redirect:/";
    }

}
