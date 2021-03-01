package com.example.aplicacion.integrationtest.controllers;

import com.example.aplicacion.Controllers.standarControllers.ContestController;
import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ContestController.class)
public class TestContestController {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	private ContestService contestService;
	@MockBean
	private SubmissionService submissionService;
	@MockBean
	private ProblemService problemService;
	@MockBean
	private LanguageService languageService;
	@MockBean
	private UserService userService;
	@MockBean
	private TeamService teamService;

	private Contest contest;

	@BeforeEach
	public void init() {
		contest = new Contest();
		contest.setId(101);
		contest.setNombreContest("elConcurso");
		contest.setDescripcion("concurso de prueba");

		when(contestService.getContest(String.valueOf(contest.getId()))).thenReturn(Optional.of(contest));
		when(contestService.getAllContests()).thenReturn(List.of(contest));
	}

	@Test
	public void testGoToContest() throws Exception {
		String incompleteURL = "/goToContest";
		String badURL = "/goToContest?contestId=523";
		String goodURL = "/goToContest?contestId=" + contest.getId() + "";
		testGetRequest(incompleteURL, badURL, goodURL);
	}

	@Test
	public void testDeleteContest() throws Exception {
		when(contestService.deleteContest(String.valueOf(contest.getId()))).thenReturn("OK");
		when(contestService.deleteContest("523")).thenReturn("contest NOT FOUND");

		String requestURL = "/deleteContest";
		String badValue = "523";
		String goodValue = String.valueOf(contest.getId());

		int status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", badValue)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(200, status);

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValue)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(302, status); // devuelve 302 --> redirección
	}

	@Test
	public void testAddUserToContest() throws Exception {
		Team team = new Team();
		team.setId(340);
		team.setNombreEquipo("test_user");

		//add user
		String requestURL = "/addUserToContest";
		String parameterName = "teamId";
		String badValueContest = "543";
		String goodValueContest = String.valueOf(contest.getId());
		String badValueTeam = "745";
		String goodValueTeam = String.valueOf(team.getId());

		when(contestService.addTeamTocontest(goodValueTeam, goodValueContest)).thenReturn("OK");
		when(contestService.addTeamTocontest(goodValueTeam, badValueContest)).thenReturn("contest NOT FOUND");
		when(contestService.addTeamTocontest(badValueTeam, goodValueContest)).thenReturn("USER NOT FOUND");
		when(contestService.addTeamTocontest(badValueTeam, badValueContest)).thenReturn("contest NOT FOUND");

		testPostRequest(requestURL, parameterName, badValueTeam, goodValueTeam);
	}

	@Test
	public void testDeleteTeamFromContest() throws Exception {
		Team team = new Team();
		team.setId(340);
		team.setNombreEquipo("test_user");

		String requestURL = "/deleteTeamFromContest";
		String parameterName = "teamId";
		String badValueContest = "543";
		String goodValueContest = String.valueOf(contest.getId());
		String badValueTeam = "745";
		String goodValueTeam = String.valueOf(team.getId());

		when(contestService.deleteTeamFromcontest(badValueContest, badValueTeam)).thenReturn("contest NOT FOUND");
		when(contestService.deleteTeamFromcontest(badValueContest, goodValueTeam)).thenReturn("contest NOT FOUND");
		when(contestService.deleteTeamFromcontest(goodValueContest, badValueTeam)).thenReturn("USER NOT FOUND");
		when(contestService.deleteTeamFromcontest(goodValueContest, goodValueTeam)).thenReturn("OK");

		testPostRequest(requestURL, parameterName, badValueTeam, goodValueTeam);

		when(contestService.deleteTeamFromcontest(goodValueContest, goodValueTeam)).thenReturn("NO ESTA EN EL CONCURSO");
		int status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param(parameterName, goodValueTeam)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(200, status); // user not in contest
	}

	@Test
	public void testAddProblemToContest() throws Exception {
		Problem problem = new Problem();
		problem.setId(211);
		problem.setNombreEjercicio("Ej 211");

		String requestURL = "/addProblemToContest";
		String parameterName = "problemId";
		String badValueProblem = "745";
		String goodValueProblem = String.valueOf(problem.getId());
		String badValueContest = "543";
		String goodValueContest = String.valueOf(contest.getId());

		when(contestService.anyadeProblemaContest(badValueContest, badValueProblem)).thenReturn("contest NOT FOUND");
		when(contestService.anyadeProblemaContest(badValueContest, goodValueProblem)).thenReturn("contest NOT FOUND");
		when(contestService.anyadeProblemaContest(goodValueContest, badValueProblem)).thenReturn("PROBLEM NOT FOUND");
		when(contestService.anyadeProblemaContest(goodValueContest, goodValueProblem)).thenReturn("OK");

		testPostRequest(requestURL, parameterName, badValueProblem, goodValueProblem);

		when(contestService.anyadeProblemaContest(goodValueContest, goodValueProblem)).thenReturn("PROBLEM DUPLICATED");
		int status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param(parameterName, goodValueProblem)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(200, status); // problem not in contest
	}

	@Test
	public void testDeleteProblemFromContest() throws Exception {
		Problem problem = new Problem();
		problem.setId(211);
		problem.setNombreEjercicio("Ej 211");

		String requestURL = "/deleteProblemFromContest";
		String parameterName = "problemId";
		String badValueProblem = "745";
		String goodValueProblem = String.valueOf(problem.getId());
		String badValueContest = "543";
		String goodValueContest = String.valueOf(contest.getId());

		when(contestService.deleteProblemFromContest(badValueContest, badValueProblem)).thenReturn("contest NOT FOUND");
		when(contestService.deleteProblemFromContest(badValueContest, goodValueProblem)).thenReturn("contest NOT FOUND");
		when(contestService.deleteProblemFromContest(goodValueContest, badValueProblem)).thenReturn("PROBLEM NOT FOUND");
		when(contestService.deleteProblemFromContest(goodValueContest, goodValueProblem)).thenReturn("OK");

		testPostRequest(requestURL, parameterName, badValueProblem, goodValueProblem);

		when(contestService.deleteProblemFromContest(goodValueContest, goodValueProblem)).thenReturn("PROBLEM NOT IN CONCURSO");
		int status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param(parameterName, goodValueProblem)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(200, status); // problem not in contest
	}

	private void testGetRequest(String incompleteURL, String badURL, String goodURL) throws Exception {
		//invalid request
		int status = mockMvc.perform(get(incompleteURL)).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(400, status);

		//valid request with wrong data
		status = mockMvc.perform(get(badURL)).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(200, status);

		//valid request
		status = mockMvc.perform(get(goodURL)).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(200, status);
	}

	private void testPostRequest(String requestURL, String name, String badValue, String goodValue) throws Exception {
		String badValueContest = "543";
		String goodValueContest = String.valueOf(contest.getId());

		int status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", badValueContest)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(400, status); // parameter missing

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param(name, badValue)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(400, status); // parameter missing


		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", badValueContest)
				.param(name, badValue)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(200, status); // contest not found

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param(name, badValue)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(200, status); // object not found

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param(name, goodValue)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(302, status); // request success and redirect
	}
}
