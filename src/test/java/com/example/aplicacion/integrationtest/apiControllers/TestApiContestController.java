package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APIContestController;
import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Pojos.ContestAPI;
import com.example.aplicacion.Pojos.ContestString;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APIContestController.class)
public class TestApiContestController {
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	MockMvc mockMvc;
	@MockBean
	private ContestService contestService;
	@MockBean
	private SubmissionService submissionService;
	@MockBean
	private ProblemService problemService;
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

		when(contestService.getContest(String.valueOf(contest.getId()))).thenReturn(Optional.of(contest));
		when(contestService.getAllContests()).thenReturn(List.of(contest));
	}

	@Test
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
		assertEquals(convertObjectToJSON(contestArray), result);
	}

	@Test
	//TODO: averiguar como probar paginación
	public void testAPIGetAllContestsWithPagination() throws Exception {
		String url = "/API/v1/contest/1";
		fail("In process!");
	}

	@Test
	public void testAPIGetContest() throws Exception {
		String badURL = "/API/v1/contest/523";
		String goodURL = "/API/v1/contest/" + contest.getId();

		String badContest = "523";
		String goodContest = String.valueOf(contest.getId());

		HttpStatus status = HttpStatus.NOT_FOUND;
		String salida = "";
		testGetContest(badURL, status, salida);

		status = HttpStatus.OK;
		salida = convertObjectToJSON(contest.toContestAPI());
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
	public void testAPIAddContest() throws Exception {
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
		salida = convertObjectToJSON(contest.toContestAPI());
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
		String result;

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

		salida = convertObjectToJSON(contest.toContestAPI());
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

	// add problem to contest


	// delete problem from contest

	private String convertObjectToJSON(Object obj) throws JsonProcessingException {
		if (obj == null) {
			throw new RuntimeException("Invalid object!");
		}
		return objectMapper.writeValueAsString(obj);
	}

	private Object convertJSONToObject(String json, Class cls) throws IOException {
		return objectMapper.readValue(json, cls);
	}
}
