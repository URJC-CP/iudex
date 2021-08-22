package es.urjc.etsii.grafo.iudex.controller.internal;

import es.urjc.etsii.grafo.iudex.entity.Contest;
import es.urjc.etsii.grafo.iudex.service.ContestService;
import es.urjc.etsii.grafo.iudex.service.ProblemService;
import es.urjc.etsii.grafo.iudex.service.TeamService;
import es.urjc.etsii.grafo.iudex.util.Sanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ContestController {
    private static final Logger logger = LoggerFactory.getLogger(ContestController.class);

    @Autowired
    private ProblemService problemService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private TeamService teamService;

    //CONCURSO html
    @GetMapping("/goToContest")
    public String goToContest(Model model, @RequestParam String contestId) {
        contestId = Sanitizer.sanitize(contestId);

        logger.debug("Get contest {}", contestId);
        Optional<Contest> contestOptional = contestService.getContestById(contestId);
        if (contestOptional.isPresent()) {
            Contest contest = contestOptional.get();
            model.addAttribute("contest", contest);
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("problems", problemService.getAllProblemas());

            logger.debug("Show contest {}", contestId);
            return "contest";

        } else {
            logger.error("Contest {} not found", contestId);
            model.addAttribute("error", "ERROR CONCURSO NO ECONTRADO");
            return "errorConocido";
        }

    }

    @PostMapping("/deleteContest")
    public String deleteConcorso(Model model, @RequestParam String contestId) {
        contestId = Sanitizer.sanitize(contestId);

        logger.debug("Delete contest {}", contestId);
        String salida = contestService.deleteContest(contestId);

        if (salida.equals("OK")) {
            logger.debug("Delete contest {} success", contestId);
            return "redirect:/";
        } else {
            logger.error("Delete contest {} failed with {}", contestId, salida);
            return "404";
        }
    }

    @PostMapping("/addUserToContest")
    public String addUserToConcuro(Model model, @RequestParam String teamId, @RequestParam String contestId) {
        contestId = Sanitizer.sanitize(contestId);
        teamId = Sanitizer.sanitize(teamId);

        logger.debug("Add user {} to contest {}", teamId, contestId);
        String salida = contestService.addTeamToContest(teamId, contestId);

        if (salida.equals("OK")) {
            logger.debug("Add user {} to contest {} success", teamId, contestId);
            return "redirect:/";
        } else {
            logger.error("Add user {} to contest {} failed with {}", teamId, salida, contestId);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/deleteTeamFromContest")
    public String deleteTeamFromContest(Model model, @RequestParam String teamId, @RequestParam String contestId) {
        teamId = Sanitizer.sanitize(teamId);
        contestId = Sanitizer.sanitize(contestId);

        logger.debug("Delete team {} from contest {}", teamId, contestId);
        String salida = contestService.deleteTeamFromContest(contestId, teamId);

        if (salida.equals("OK")) {
            logger.debug("Delete team {} from contest {} success", teamId, contestId);
            return "redirect:/";
        } else {
            logger.error("Delete team {} from contest {} failed with {}", teamId, contestId, salida);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/addProblemToContest")
    public String addProblemToContest(Model model, @RequestParam String problemId, @RequestParam String contestId) {
        problemId = Sanitizer.sanitize(problemId);
        contestId = Sanitizer.sanitize(contestId);

        logger.debug("Add problem {} to contest {}", problemId, contestId);
        String salida = contestService.anyadeProblemaContest(contestId, problemId);

        if (salida.equals("OK")) {
            logger.debug("Add problem {} to contest {} success", problemId, contestId);
            return "redirect:/";
        } else {
            logger.debug("Add problem {} to contest {} failed with {}", problemId, contestId, salida);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/deleteProblemFromContest")
    public String deleteProblemFromContest(Model model, @RequestParam String problemId, @RequestParam String contestId) {
        problemId = Sanitizer.sanitize(problemId);
        contestId = Sanitizer.sanitize(contestId);

        logger.debug("Delete problem {} from contest {}", problemId, contestId);
        String salida = contestService.deleteProblemFromContest(contestId, problemId);

        if (salida.equals("OK")) {
            logger.debug("Delete problem {} from contest {} success", problemId, contestId);
            return "redirect:/";
        } else {
            logger.debug("Delete problem {} from contest {} failed with {}", problemId, contestId, salida);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
}