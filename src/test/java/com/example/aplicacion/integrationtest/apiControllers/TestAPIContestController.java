package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APIContestController;
import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Pojos.ContestAPI;
import com.example.aplicacion.Pojos.ContestString;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APIContestController.class)
public class TestAPIContestController {
	private final JSONConverter jsonConverter = new JSONConverter();
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ContestService contestService;
	private Contest contest;
	private Team owner;
	private Problem problem;

	@BeforeEach
	public void init() {
		contest = new Contest();
		contest.setId(101);
		contest.setNombreContest("elConcurso");
		contest.setDescripcion("concurso de prueba");

		owner = new Team();
		owner.setId(201);
		owner.setNombreEquipo("propietario");
		contest.setTeamPropietario(owner);

		problem = new Problem();
		problem.setId(244);
		problem.setNombreEjercicio("Ejercicio de prueba");

		when(contestService.getContest(String.valueOf(contest.getId()))).thenReturn(Optional.of(contest));
		when(contestService.getAllContests()).thenReturn(List.of(contest));
	}

	@Test
	@DisplayName("Get All Contests")
	public void testAPIGetAllContests() throws Exception {
		String url = "/API/v1/contest/";
		String result = mockMvc.perform(
			get(url).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn()
			.getResponse()
			.getContentAsString();

		ContestAPI[] contestArray = new ContestAPI[1];
		contestArray[0] = contest.toContestAPI();
		assertEquals(jsonConverter.convertObjectToJSON(contestArray), result);
	}

	@Test
	@DisplayName("Get All Contests with Pagination")
	@Disabled("Get All Contests with Pagination - Null Pointer Exception from ContestService")
	public void testAPIGetAllContestsWithPagination() throws Exception {
		String url = "/API/v1/contest/page";
		Pageable pageable = PageRequest.of(1, 5);
		PageImpl<Contest> page = new PageImpl<>(List.of(contest));
		when(contestService.getContestPage(pageable)).thenReturn(page);
		mockMvc.perform(
			get(url)
				.characterEncoding("utf-8")
				.param("pageable", String.valueOf(pageable))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$['pageable']['paged']").value("true"));
	}

	@Test
	@DisplayName("Get One Contest")
	public void testAPIGetContest() throws Exception {
		String badContest = "523";
		String goodContest = String.valueOf(contest.getId());

		String badURL = "/API/v1/contest/" + badContest;
		String goodURL = "/API/v1/contest/" + goodContest;

		HttpStatus status = HttpStatus.NOT_FOUND;
		String salida = "";
		testGetContest(badURL, status, salida);

		status = HttpStatus.OK;
		salida = jsonConverter.convertObjectToJSON(contest.toContestAPI());
		testGetContest(goodURL, status, salida);
	}

	private void testGetContest(String url, HttpStatus status, String salida) throws Exception {
		String result;
		result = mockMvc.perform(
			get(url).accept(MediaType.APPLICATION_JSON)
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}

	@Test
	@DisplayName("Create Contest")
	public void testAPICreateContest() throws Exception {
		String url = "/API/v1/contest";

		String badContest = "521";
		String goodContest = contest.getNombreContest();
		String badTeam = "643";
		String goodTeam = String.valueOf(owner.getId());
		String description = contest.getDescripcion();

		ContestString cs = new ContestString();
		HttpStatus status = HttpStatus.NOT_FOUND;

		String salida = "contest NAME DUPLICATED";
		cs.setSalida(salida);
		when(contestService.creaContest(badContest, badTeam, Optional.of(description))).thenReturn(cs);
		testAddContest(url, badContest, badTeam, description, status, salida);
		when(contestService.creaContest(badContest, goodTeam, Optional.of(description))).thenReturn(cs);
		testAddContest(url, badContest, goodTeam, description, status, salida);

		salida = "TEAM NOT FOUND";
		cs.setSalida(salida);
		when(contestService.creaContest(goodContest, badTeam, Optional.of(description))).thenReturn(cs);
		testAddContest(url, goodContest, badTeam, description, status, salida);

		cs.setSalida("OK");
		status = HttpStatus.CREATED;
		salida = jsonConverter.convertObjectToJSON(contest.toContestAPI());
		cs.setContest(contest);
		when(contestService.creaContest(goodContest, goodTeam, Optional.of(description))).thenReturn(cs);
		testAddContest(url, goodContest, goodTeam, description, status, salida);
	}

	private void testAddContest(String url, String contest, String team, String description, HttpStatus status, String salida) throws Exception {
		String result;
		result = mockMvc.perform(
			post(url)
				.characterEncoding("utf8")
				.param("contestName", contest)
				.param("teamId", team)
				.param("descripcion", description)
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}

	@Test
	@DisplayName("Delete Contest")
	public void testAPIDeleteContest() throws Exception {
		String badURL = "/API/v1/contest/521";
		String goodURL = "/API/v1/contest/" + contest.getId();

		String badContest = "521";
		String goodContest = String.valueOf(contest.getId());
		HttpStatus status = HttpStatus.NOT_FOUND;
		String salida = "contest NOT FOUND";

		when(contestService.deleteContest(badContest)).thenReturn(salida);
		testDeleteContest(badURL, status, salida);

		status = HttpStatus.OK;
		salida = "";
		when(contestService.deleteContest(goodContest)).thenReturn("OK");
		testDeleteContest(goodURL, status, salida);
	}

	private void testDeleteContest(String url, HttpStatus status, String salida) throws Exception {
		String result;
		result = mockMvc.perform(
			delete(url)
				.characterEncoding("utf8")
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse().getContentAsString();
		System.out.println(result);
		assertEquals(salida, result);
	}

	@Test
	@DisplayName("Update Contest")
	public void testAPIUpdateContest() throws Exception {
		String badURL = "/API/v1/contest/521";
		String goodURL = "/API/v1/contest/" + contest.getId();

		String badContest = "521";
		String goodContest = String.valueOf(contest.getId());
		Optional<String> badName = Optional.of("654");
		Optional<String> goodName = Optional.of(contest.getNombreContest());
		Optional<String> badTeam = Optional.of("435");
		Optional<String> goodTeam = Optional.of(String.valueOf(owner.getId()));
		Optional<String> description = Optional.of(contest.getDescripcion());

		ContestString cs = new ContestString();
		HttpStatus status = HttpStatus.NOT_FOUND;
		String salida = "CONTEST ID DOES NOT EXIST";

		cs.setSalida(salida);
		when(contestService.updateContest(badContest, badName, badTeam, description)).thenReturn(cs);
		testUpdateContest(badURL, badName.get(), badTeam.get(), description.get(), status, salida);

		when(contestService.updateContest(badContest, goodName, badTeam, description)).thenReturn(cs);
		testUpdateContest(badURL, goodName.get(), badTeam.get(), description.get(), status, salida);

		when(contestService.updateContest(badContest, badName, goodTeam, description)).thenReturn(cs);
		testUpdateContest(badURL, badName.get(), goodTeam.get(), description.get(), status, salida);

		salida = "CONTEST NAME DUPLICATED";
		cs.setSalida(salida);
		when(contestService.updateContest(goodContest, badName, badTeam, description)).thenReturn(cs);
		testUpdateContest(goodURL, badName.get(), badTeam.get(), description.get(), status, salida);

		when(contestService.updateContest(goodContest, badName, goodTeam, description)).thenReturn(cs);
		testUpdateContest(goodURL, badName.get(), goodTeam.get(), description.get(), status, salida);

		salida = "TEAM NOT FOUND";
		cs.setSalida(salida);
		when(contestService.updateContest(goodContest, goodName, badTeam, description)).thenReturn(cs);
		testUpdateContest(goodURL, goodName.get(), badTeam.get(), description.get(), status, salida);

		salida = jsonConverter.convertObjectToJSON(contest.toContestAPI());
		status = HttpStatus.CREATED;
		cs.setSalida("OK");
		cs.setContest(contest);
		when(contestService.updateContest(goodContest, goodName, goodTeam, description)).thenReturn(cs);
		testUpdateContest(goodURL, goodName.get(), goodTeam.get(), description.get(), status, salida);
	}

	private void testUpdateContest(String url, String contestName, String teamId, String description, HttpStatus status, String salida) throws Exception {
		String result;
		result = mockMvc.perform(
			put(url)
				.characterEncoding("utf8")
				.param("contestName", contestName)
				.param("teamId", teamId)
				.param("descripcion", description)
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse().getContentAsString();
		assertEquals(salida, result);
	}

	@Test
	@DisplayName("Add Problem to Contest")
	public void testAPIAddProblemToContest() throws Exception {
		String badURL = "/API/v1/contest/532/756";
		String badURL2 = "/API/v1/contest/532/" + problem.getId();
		String badURL3 = "/API/v1/contest/" + contest.getId() + "/756";
		String goodURL = "/API/v1/contest/" + contest.getId() + "/" + problem.getId();

		String badContest = "532";
		String goodContest = String.valueOf(contest.getId());
		String badProblem = "756";
		String goodProblem = String.valueOf(problem.getId());

		HttpStatus status = HttpStatus.NOT_FOUND;
		String salida = "contest NOT FOUND";
		when(contestService.anyadeProblemaContest(badContest, badProblem)).thenReturn(salida);
		testAddProblem(badURL, status, salida);
		when(contestService.anyadeProblemaContest(badContest, goodProblem)).thenReturn(salida);
		testAddProblem(badURL2, status, salida);

		salida = "PROBLEM NOT FOUND";
		when(contestService.anyadeProblemaContest(goodContest, badProblem)).thenReturn(salida);
		testAddProblem(badURL3, status, salida);

		salida = "PROBLEM DUPLICATED";
		when(contestService.anyadeProblemaContest(goodContest, badProblem)).thenReturn(salida);
		testAddProblem(badURL3, status, salida);

		status = HttpStatus.OK;
		salida = "OK";
		when(contestService.anyadeProblemaContest(goodContest, goodProblem)).thenReturn(salida);
		salida = "";
		testAddProblem(goodURL, status, salida);
	}

	private void testAddProblem(String url, HttpStatus status, String salida) throws Exception {
		String result;
		result = mockMvc.perform(
			post(url).characterEncoding("utf8")
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}

	@Test
	@DisplayName("Delete Problem From Contest")
	public void testAPIDeleteProblemFromContest() throws Exception {
		String badURL = "/contest/531/764";
		String badURL2 = "/contest/531/" + problem.getId();
		String badURL3 = "/contest/" + contest.getId() + "/764";
		String goodURL = "/contest/" + contest.getId() + "/" + problem.getId();

		String badContest = "531";
		String goodContest = String.valueOf(contest.getId());
		String badProblem = "764";
		String goodProblem = String.valueOf(problem.getId());

		HttpStatus status = HttpStatus.NOT_FOUND;
		String salida = "contest NOT FOUND";
		when(contestService.deleteProblemFromContest(badContest, badProblem)).thenReturn(salida);
		testDeleteProblem(badURL, status, salida);
		when(contestService.deleteProblemFromContest(badContest, goodProblem)).thenReturn(salida);
		testDeleteProblem(badURL2, status, salida);

		salida = "PROBLEM NOT FOUND";
		when(contestService.deleteProblemFromContest(goodContest, badProblem)).thenReturn(salida);
		testDeleteProblem(badURL3, status, salida);

		salida = "PROBLEM NOT IN CONCURSO";
		when(contestService.deleteProblemFromContest(goodContest, badProblem)).thenReturn(salida);
		testDeleteProblem(badURL3, status, salida);

		salida = "OK";
		status = HttpStatus.OK;
		when(contestService.deleteProblemFromContest(goodContest, goodProblem)).thenReturn(salida);
		salida = "";
		testDeleteProblem(goodURL, status, salida);
	}

	private void testDeleteProblem(String url, HttpStatus status, String salida) throws Exception {
		String result;
		result = mockMvc.perform(
			delete(url)
				.characterEncoding("utf8")
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}
}