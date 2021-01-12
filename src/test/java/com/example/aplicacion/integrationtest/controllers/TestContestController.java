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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
	public void testGetContest() {
		assertThat(contestService.getContest(String.valueOf(contest.getId())), equalTo(Optional.of(contest)));
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
		).andReturn().getResponse().getStatus();
		assertEquals(200, status);

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValue)
		).andReturn().getResponse().getStatus();
		assertEquals(302, status); // devuelve 302 --> redirección
	}

	@Test
	public void testAddUserToContest() throws Exception {
		Team team = new Team();
		team.setId(340);
		team.setNombreEquipo("test_user");

		//add user
		String requestURL = "/addUserToContest";
		String badValueContest = "543";
		String goodValueContest = String.valueOf(contest.getId());
		String badValueTeam = "745";
		String goodValueTeam = String.valueOf(team.getId());

		when(contestService.addTeamTocontest(goodValueTeam, goodValueContest)).thenReturn("OK");
		when(contestService.addTeamTocontest(goodValueTeam, badValueContest)).thenReturn("contest NOT FOUND");
		when(contestService.addTeamTocontest(badValueTeam, goodValueContest)).thenReturn("USER NOT FOUND");
		when(contestService.addTeamTocontest(badValueTeam, badValueContest)).thenReturn("contest NOT FOUND");

		int status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", badValueContest)
		).andReturn().getResponse().getStatus();
		assertEquals(400, status); // teamId is not present

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("teamId", badValueTeam)
		).andReturn().getResponse().getStatus();
		assertEquals(400, status); // userId is not present


		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", badValueContest)
				.param("teamId", badValueTeam)
		).andReturn().getResponse().getStatus();
		assertEquals(200, status); // contest not found

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param("teamId", badValueTeam)
		).andReturn().getResponse().getStatus();
		assertEquals(200, status); // team not found

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param("teamId", goodValueTeam)
		).andReturn().getResponse().getStatus();
		assertEquals(302, status); // redirección
	}

	@Test
	public void testDeleteTeamFromContest() throws Exception {
		Team team = new Team();
		team.setId(340);
		team.setNombreEquipo("test_user");

		//add user
		String requestURL = "/deleteTeamFromContest";
		String badValueContest = "543";
		String goodValueContest = String.valueOf(contest.getId());
		String badValueTeam = "745";
		String goodValueTeam = String.valueOf(team.getId());

		when(contestService.deleteTeamFromcontest(badValueContest, badValueTeam)).thenReturn("contest NOT FOUND");
		when(contestService.deleteTeamFromcontest(badValueContest, goodValueTeam)).thenReturn("contest NOT FOUND");
		when(contestService.deleteTeamFromcontest(goodValueContest, badValueTeam)).thenReturn("USER NOT FOUND");
		when(contestService.deleteTeamFromcontest(goodValueContest, goodValueTeam)).thenReturn("NO ESTA EN EL CONCURSO");

		int status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", badValueContest)
		).andReturn().getResponse().getStatus();
		assertEquals(400, status); // teamId is not present

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("teamId", badValueTeam)
		).andReturn().getResponse().getStatus();
		assertEquals(400, status); // userId is not present


		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", badValueContest)
				.param("teamId", badValueTeam)
		).andReturn().getResponse().getStatus();
		assertEquals(200, status); // contest not found

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param("teamId", badValueTeam)
		).andReturn().getResponse().getStatus();
		assertEquals(200, status); // team not found

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param("teamId", goodValueTeam)
		).andReturn().getResponse().getStatus();
		assertEquals(200, status); // user not in contest

		when(contestService.deleteTeamFromcontest(goodValueContest, goodValueTeam)).thenReturn("OK");
		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param("teamId", goodValueTeam)
		).andReturn().getResponse().getStatus();
		assertEquals(302, status); // redirección
	}

	@Test
	public void testAddProblemToContest() throws Exception {
		Problem problem = new Problem();
		problem.setId(211);
		problem.setNombreEjercicio("Ej 211");

		String requestURL = "/addProblemToContest";
		String badValueProblem = "745";
		String goodValueProblem = String.valueOf(problem.getId());
		String badValueContest = "543";
		String goodValueContest = String.valueOf(contest.getId());

	}

	@Test
	public void testDeleteProblemFromContest() {
		Problem problem = new Problem();
		problem.setId(211);
		problem.setNombreEjercicio("Ej 211");


		//add problem
		String requestURL = "/addProblemToContest";
		String badValueProblem = "745";
		String goodValueProblem = String.valueOf(problem.getId());
		String badValueContest = "543";
		String goodValueContest = String.valueOf(contest.getId());
	}

	private void testGetRequest(String incompleteURL, String badURL, String goodURL) throws Exception {
		//invalid request
		int status = mockMvc.perform(get(incompleteURL)).andReturn().getResponse().getStatus();
		assertEquals(400, status);

		//valid request with wrong data
		status = mockMvc.perform(get(badURL)).andReturn().getResponse().getStatus();
		assertEquals(200, status);

		//valid request
		status = mockMvc.perform(get(goodURL)).andReturn().getResponse().getStatus();
		assertEquals(200, status);
	}
}
