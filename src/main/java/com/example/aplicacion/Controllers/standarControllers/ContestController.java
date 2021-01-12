package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.services.*;
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
	Logger logger = LoggerFactory.getLogger(ContestController.class);
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
	public String goToContest(Model model, @RequestParam String contestId) {
		logger.debug("Get request received for contest " + contestId);
		Optional<Contest> contest = contestService.getContest(contestId);
		if (contest.isEmpty()) {
			logger.error("Contest " + contestId + " not found");
			model.addAttribute("error", "ERROR CONCURSO NO ECONTRADO");
			return "errorConocido";
		}
		model.addAttribute("contest", contest.get());
		model.addAttribute("teams", teamService.getAllTeams());
		model.addAttribute("problems", problemService.getAllProblemas());

		logger.debug("Showing contest " + contestId);
		return "contest";
	}

	@PostMapping("/deleteContest")
	public String deleteConcorso(Model model, @RequestParam String contestId) {
		logger.debug("Delete contest " + contestId + " request received");
		String salida = contestService.deleteContest(contestId);

		if (salida.equals("OK")) {
			logger.debug("Delete contest " + contestId + " request success");
			return "redirect:/";
		} else {
			logger.error("Delete contest " + contestId + " request failed with " + salida);
			return "404";
		}
	}

	@PostMapping("/addUserToContest")
	public String addUserToConcuro(Model model, @RequestParam String teamId, @RequestParam String contestId) {
		logger.debug("Add user " + teamId + " request receiver for contest " + contestId);
		String salida = contestService.addTeamTocontest(teamId, contestId);

		if (salida.equals("OK")) {
			logger.debug("Add user " + teamId + " request success for contest " + contestId);
			return "redirect:/";
		} else {
			logger.error("Add user " + teamId + " request failed with " + salida);
			model.addAttribute("error", salida);
			return "errorConocido";
		}
	}

	@PostMapping("/deleteTeamFromContest")
	public String deleteTeamFromContest(Model model, @RequestParam String teamId, @RequestParam String contestId) {
		logger.debug("Delete team " + teamId + " request received for contest " + contestId);
		String salida = contestService.deleteTeamFromcontest(contestId, teamId);

		if (salida.equals("OK")) {
			logger.debug("Delete team " + teamId + " request success for contest " + contestId);
			return "redirect:/";
		} else {
			logger.error("Delete team " + teamId + " request failed with " + salida);
			model.addAttribute("error", salida);
			return "errorConocido";
		}
	}

	@PostMapping("/addProblemToContest")
	public String addProblemToContest(Model model, @RequestParam String problemId, @RequestParam String contestId) {
		logger.debug("Add problem " + problemId + " request received for contest " + contestId);
		String salida = contestService.anyadeProblemaContest(contestId, problemId);

		if (salida.equals("OK")) {
			logger.debug("Add problem " + problemId + " request success for contest " + contestId);
			return "redirect:/";
		} else {
			logger.error("Add problem " + problemId + " request failed with " + salida);
			model.addAttribute("error", salida);
			return "errorConocido";
		}
	}

	@PostMapping("/deleteProblemFromContest")
	public String deleteProblemFromContest(Model model, @RequestParam String problemId, @RequestParam String contestId) {
		logger.debug("Delete problem " + problemId + " request received for contest " + contestId);
		String salida = contestService.deleteProblemFromContest(contestId, problemId);

		if (salida.equals("OK")) {
			logger.debug("Delete problem " + problemId + " request success for contest " + contestId);
			return "redirect:/";
		} else {
			logger.debug("Delete problem " + problemId + " request failed with " + salida);
			model.addAttribute("error", salida);
			return "errorConocido";
		}
	}
}
