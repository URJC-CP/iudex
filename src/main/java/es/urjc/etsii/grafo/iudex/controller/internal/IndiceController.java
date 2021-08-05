package es.urjc.etsii.grafo.iudex.controller.internal;

import es.urjc.etsii.grafo.iudex.entity.Submission;
import es.urjc.etsii.grafo.iudex.pojo.SubmissionStringResult;
import es.urjc.etsii.grafo.iudex.service.*;
import es.urjc.etsii.grafo.iudex.util.Sanitizer;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
public class IndiceController {
    private static final Logger logger = LoggerFactory.getLogger(IndiceController.class);

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
        problemaAsignado = Sanitizer.sanitize(problemaAsignado);
        lenguaje = Sanitizer.sanitize(lenguaje);
        teamId = Sanitizer.sanitize(teamId);
        contestId = Sanitizer.sanitize(contestId);

        logger.debug("Submit {} code to problem {}", lenguaje, problemaAsignado);
        String fileName = FilenameUtils.removeExtension(codigo.getOriginalFilename());
        String cod = new String(codigo.getBytes());

        logger.debug("Running submission");
        //Crea la submission
        SubmissionStringResult salida = submissionService.creaYejecutaSubmission(cod, problemaAsignado, lenguaje, fileName, contestId, teamId);

        if (salida.getSalida().equals("OK")) {
            logger.debug("Run submission {} success", salida.getSubmission().getId());
            return "redirect:/";
        } else {
            logger.debug("Run submission {} failed with {}", salida.getSubmission().getId(), salida.getSalida());
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
        problemId = Sanitizer.sanitize(problemId);
        contestId = Sanitizer.sanitize(contestId);

        logger.debug("Add problem {} to contest {}", problemId, contestId);
        String salida = contestService.anyadeProblemaContest(contestId, problemId);

        if (salida.equals("OK")) {
            logger.debug("Add problem {} to contest {} success", problemId, contestId);
        } else {
            logger.debug("Add problem {} to contest {} failed with {}", problemId, contestId, salida);
        }
        return "indexOriginal";
    }

    @PostMapping("/creaUsuario")
    public String creaUsuario(Model model, @RequestParam String userNickname, @RequestParam String userMail) {
        userNickname = Sanitizer.sanitize(userNickname);
        userMail = Sanitizer.sanitize(userMail);

        logger.debug("Create user {}", userNickname);
        String salida = userService.crearUsuario(userNickname, userMail).getSalida();

        if (salida.equals("OK")) {
            logger.debug("Create user {} success", userNickname);
            return "redirect:/";
        } else {
            logger.error("Create user {} failed with {} ", userNickname, salida);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/creaContest")
    public String creaContest(Model model, @RequestParam String contestName, @RequestParam String teamId, @RequestParam Optional<String> descripcion) {
        contestName = Sanitizer.sanitize(contestName);
        teamId = Sanitizer.sanitize(teamId);
        descripcion = Sanitizer.sanitize(descripcion);

        logger.debug("Create contest {}", contestName);

        long startDateTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        long endDateTime = LocalDateTime.now().plusDays(1).atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();

        String salida = contestService.creaContest(contestName, teamId, descripcion, startDateTime, endDateTime).getSalida();

        if (salida.equals("OK")) {
            logger.debug("Create contest {} success", contestName);
        } else {
            logger.error("Create contest {} failed with {} ", contestName, salida);
        }
        return "redirect:/";
    }

}
