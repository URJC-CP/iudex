package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import static com.example.aplicacion.utils.Sanitizer.sanitize;

@Controller
public class ContestController {
    Logger logger = LoggerFactory.getLogger(ContestController.class);
    @Autowired
    private ProblemService problemService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private TeamService teamService;

    //CONCURSO html
    @GetMapping("/goToContest")
    public String goToContest(Model model, @RequestParam String contestId) {
        contestId = sanitize(contestId);

        logger.debug("Get contest {0}", contestId);
        Optional<Contest> contestOptional = contestService.getContestById(contestId);
        if (contestOptional.isPresent()) {
            Contest contest = contestOptional.get();
            model.addAttribute("contest", contest);
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("problems", problemService.getAllProblemas());

            logger.debug("Show contest {0}", contestId);
            return "contest";

        } else {
            logger.error("Contest {0} not found", contestId);
            model.addAttribute("error", "ERROR CONCURSO NO ECONTRADO");
            return "errorConocido";
        }

    }

    @PostMapping("/deleteContest")
    public String deleteConcorso(Model model, @RequestParam String contestId) {
        contestId = sanitize(contestId);

        logger.debug("Delete contest {0}", contestId);
        String salida = contestService.deleteContest(contestId);

        if (salida.equals("OK")) {
            logger.debug("Delete contest {0} success", contestId);
            return "redirect:/";
        } else {
            logger.error("Delete contest {0} failed with {1}", contestId, salida);
            return "404";
        }
    }

    @PostMapping("/addUserToContest")
    public String addUserToConcuro(Model model, @RequestParam String teamId, @RequestParam String contestId) {
        contestId = sanitize(contestId);

        logger.debug("Add user {0} to contest {1}", teamId, contestId);
        String salida = contestService.addTeamToContest(teamId, contestId);

        if (salida.equals("OK")) {
            logger.debug("Add user {0} to contest {1} success", teamId, contestId);
            return "redirect:/";
        } else {
            logger.error("Add user {0} to contest {2} failed with {1}", teamId, salida, contestId);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/deleteTeamFromContest")
    public String deleteTeamFromContest(Model model, @RequestParam String teamId, @RequestParam String contestId) {
        teamId = sanitize(teamId);
        contestId = sanitize(contestId);

        logger.debug("Delete team {0} from contest {1}", teamId, contestId);
        String salida = contestService.deleteTeamFromContest(contestId, teamId);

        if (salida.equals("OK")) {
            logger.debug("Delete team {0} from contest {1} success", teamId, contestId);
            return "redirect:/";
        } else {
            logger.error("Delete team {0} from contest {1} failed with {2}", teamId, contestId, salida);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/addProblemToContest")
    public String addProblemToContest(Model model, @RequestParam String problemId, @RequestParam String contestId) {
        problemId = sanitize(problemId);
        contestId = sanitize(contestId);

        logger.debug("Add problem {0} to contest {1}", problemId, contestId);
        String salida = contestService.anyadeProblemaContest(contestId, problemId);

        if (salida.equals("OK")) {
            logger.debug("Add problem {0} to contest {1} success", problemId, contestId);
            return "redirect:/";
        } else {
            logger.debug("Add problem {0} to contest {1} failed with {2}", problemId, contestId, salida);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }

    @PostMapping("/deleteProblemFromContest")
    public String deleteProblemFromContest(Model model, @RequestParam String problemId, @RequestParam String contestId) {
        problemId = sanitize(problemId);
        contestId = sanitize(contestId);

        logger.debug("Delete problem {0} from contest {1}", problemId, contestId);
        String salida = contestService.deleteProblemFromContest(contestId, problemId);

        if (salida.equals("OK")) {
            logger.debug("Delete problem {0} from contest {1} success", problemId, contestId);
            return "redirect:/";
        } else {
            logger.debug("Delete problem {0} from contest {1} failed with {2}", problemId, contestId, salida);
            model.addAttribute("error", salida);
            return "errorConocido";
        }
    }
}
