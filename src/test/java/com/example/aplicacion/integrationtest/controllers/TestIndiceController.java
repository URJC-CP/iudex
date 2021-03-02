package com.example.aplicacion.integrationtest.controllers;

import com.example.aplicacion.Controllers.standarControllers.IndiceController;
import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import com.example.aplicacion.Pojos.ContestString;
import com.example.aplicacion.Pojos.SubmissionStringResult;
import com.example.aplicacion.Pojos.UserString;
import com.example.aplicacion.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(IndiceController.class)
public class TestIndiceController {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private RabbitTemplate rabbitTemplate;

	@MockBean
	private SubmissionService submissionService;
	@MockBean
	private ProblemService problemService;
	@MockBean
	private LanguageService languageService;
	@MockBean
	private ContestService contestService;
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
	public void testHomePageRedirection() throws Exception {
		mockMvc.perform(get("/"))
			.andDo(print())
			.andExpect(view().name("indexOriginal"));
	}

	//@Test
	//@TODO: hay que ver como se puede probar esta parte
	public void testSubmission() throws Exception {
		Problem problem = new Problem();
		problem.setId(211);
		problem.setNombreEjercicio("Ej 211");

		String badContest = null, goodContest = null;
		String badProblem = null, goodProblem = null;
		String supportedLanguage = null, unSupportedLanguage = null;
		String badTeam = null, goodTeam = null;
		MultipartFile badCode = null, goodCode = null;
		String badFile = null, goodFile = null;
		SubmissionStringResult salida = new SubmissionStringResult();

		// concurso
		salida.setSalida("CONCURSO NOT FOUND");
		when(submissionService
			.creaYejecutaSubmission(String.valueOf(badCode.getBytes()), badProblem, unSupportedLanguage, badFile, badContest, badTeam))
			.thenReturn(salida);
		String msg = mockMvc.perform(post("/answerSubida")).andDo(print())
			.andReturn().getResponse().getErrorMessage();
		assertEquals("errorConocido", msg);

		//problema
		salida.setSalida("PROBLEM NOT FOUND");
		when(submissionService
			.creaYejecutaSubmission(String.valueOf(badCode.getBytes()), badProblem, unSupportedLanguage, badFile, goodContest, badTeam))
			.thenReturn(salida);
		//equipo o usuario
		salida.setSalida("TEAM NOT FOUND");
		when(submissionService
			.creaYejecutaSubmission(String.valueOf(badCode.getBytes()), goodProblem, unSupportedLanguage, badFile, goodContest, badTeam))
			.thenReturn(salida);
		//lenguaje
		salida.setSalida("LANGUAGE NOT FOUND");
		when(submissionService
			.creaYejecutaSubmission(String.valueOf(badCode.getBytes()), goodProblem, unSupportedLanguage, badFile, goodContest, goodTeam))
			.thenReturn(salida);
	}

	//@Test
	//@TODO: hay que ver como se puede probar esta parte
	public void testScoreBoard() {
	}

	@Test
	public void testAddProblemToContest() throws Exception {
		Problem problem = new Problem();
		problem.setId(211);
		problem.setNombreEjercicio("Ej 211");

		String requestURL = "/asignaProblemaAContest";
		String badValueProblem = "745";
		String goodValueProblem = String.valueOf(problem.getId());
		String badValueContest = "523";
		String goodValueContest = String.valueOf(contest.getId());

		when(contestService.anyadeProblemaContest(badValueContest, badValueProblem)).thenReturn("contest NOT FOUND");
		when(contestService.anyadeProblemaContest(badValueContest, goodValueProblem)).thenReturn("contest NOT FOUND");
		when(contestService.anyadeProblemaContest(goodValueContest, badValueProblem)).thenReturn("PROBLEM NOT FOUND");
		when(contestService.anyadeProblemaContest(goodValueContest, goodValueProblem)).thenReturn("OK");

		int status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", badValueContest)
		).andDo(print())
			.andReturn().getResponse().getStatus();
		assertEquals(400, status); // parameter missing

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("problemId", badValueProblem)
		).andDo(print())
			.andReturn().getResponse().getStatus();
		assertEquals(400, status); // parameter missing


		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", badValueContest)
				.param("problemId", badValueProblem)
		).andDo(print())
			.andExpect(view().name("indexOriginal"))
			.andReturn().getResponse().getStatus();
		assertEquals(200, status); // contest not found

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param("problemId", badValueProblem)
		).andDo(print())
			.andExpect(view().name("indexOriginal"))
			.andReturn().getResponse().getStatus();
		assertEquals(200, status); // object not found

		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param("problemId", goodValueProblem)
		).andDo(print())
			.andExpect(view().name("indexOriginal"))
			.andReturn().getResponse().getStatus();
		assertEquals(200, status); // request success and redirect

		when(contestService.anyadeProblemaContest(goodValueContest, goodValueProblem)).thenReturn("PROBLEM DUPLICATED");
		status = mockMvc.perform(
			post(requestURL)
				.characterEncoding("utf8")
				.param("contestId", goodValueContest)
				.param("problemId", goodValueProblem)
		).andDo(print())
			.andExpect(view().name("indexOriginal"))
			.andReturn().getResponse().getStatus();
		assertEquals(200, status); // problem not in contest
	}

	@Test
	public void testCreateUser() throws Exception {
		String requestURL = "/creaUsuario";

		User user = new User();
		user.setId(666);
		user.setNickname("usuario de prueba");

		String badValueNickname = "nickname";
		String badValueMail = "mail";
		String goodValueNickname = "usuario de prueba";
		String goodValueMail = "prueba@pruebasthejudge.com";

		UserString us = new UserString();

		int status = mockMvc.perform(
			post(requestURL).param("userNickname", badValueNickname)
		)
			.andDo(print())
			.andReturn().getResponse().getStatus();
		assertEquals(400, status); // user mail param missing

		status = mockMvc.perform(
			post(requestURL).param("userMail", badValueMail)
		)
			.andDo(print())
			.andReturn().getResponse().getStatus();
		assertEquals(400, status); // user nickname param missing


		us.setSalida("USER NICKNAME DUPLICATED");
		when(userService.crearUsuario(badValueNickname, badValueMail)).thenReturn(us);
		when(userService.crearUsuario(badValueNickname, goodValueMail)).thenReturn(us);
		status = mockMvc.perform(
			post(requestURL)
				.param("userNickname", badValueNickname)
				.param("userMail", badValueMail)
		)
			.andDo(print())
			.andReturn().getResponse().getStatus();
		assertEquals(200, status);

		status = mockMvc.perform(
			post(requestURL)
				.param("userNickname", badValueNickname)
				.param("userMail", goodValueMail)
		)
			.andDo(print())
			.andReturn().getResponse().getStatus();
		assertEquals(200, status);

		us.setSalida("USER MAIL DUPLICATED");
		when(userService.crearUsuario(goodValueNickname, badValueMail)).thenReturn(us);
		status = mockMvc.perform(
			post(requestURL)
				.param("userNickname", goodValueNickname)
				.param("userMail", badValueMail)
		)
			.andDo(print())
			.andReturn().getResponse().getStatus();
		assertEquals(200, status);

		us.setSalida("OK");
		when(userService.crearUsuario(goodValueNickname, goodValueMail)).thenReturn(us);
		status = mockMvc.perform(
			post(requestURL)
				.param("userNickname", goodValueNickname)
				.param("userMail", goodValueMail)
		)
			.andDo(print())
			.andReturn().getResponse().getStatus();
		assertEquals(302, status); // create user success and redirect
	}

	@Test
	public void testCreateContest() throws Exception {
		String requestURL = "/creaContest";

		Team team = new Team();
		team.setId(6667);
		team.setNombreEquipo("Equipo de prueba");

		String badValueContest = "523";
		String goodValueContestName = contest.getNombreContest();
		String badValueTeam = "7777";
		String goodValueTeam = String.valueOf(team.getId());
		String badValueContestName = "concurso falso";
		String badValueDesc = "";
		Optional<String> goodValueDesc = Optional.of(contest.getDescripcion());

		ContestString contestString = new ContestString();
		contestString.setSalida("contest NAME DUPLICATED");
		when(contestService.creaContest(badValueContestName, badValueTeam, Optional.of(badValueDesc))).thenReturn(contestString);

		int status = mockMvc.perform(
			post(requestURL)
				.param("contestName", badValueContestName)
				.param("teamId", badValueTeam)
				.param("descripcion", badValueDesc)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(302, status);

		contestString.setSalida("TEAM NOT FOUND");
		when(contestService.creaContest(goodValueContestName, badValueTeam, Optional.of(badValueDesc))).thenReturn(contestString);

		status = mockMvc.perform(
			post(requestURL)
				.param("contestName", goodValueContestName)
				.param("teamId", badValueTeam)
				.param("descripcion", badValueDesc)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(302, status);

		contestString.setSalida("OK");
		when(contestService.creaContest(goodValueContestName, goodValueTeam, Optional.of(badValueDesc))).thenReturn(contestString);
		when(contestService.creaContest(goodValueContestName, goodValueTeam, goodValueDesc)).thenReturn(contestString);

		status = mockMvc.perform(
			post(requestURL)
				.param("contestName", goodValueContestName)
				.param("teamId", goodValueTeam)
				.param("descripcion", badValueDesc)
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(302, status);

		status = mockMvc.perform(
			post(requestURL)
				.param("contestName", goodValueContestName)
				.param("teamId", goodValueTeam)
				.param("descripcion", goodValueDesc.get())
		).andDo(print()).andReturn().getResponse().getStatus();
		assertEquals(302, status);

	}
}
