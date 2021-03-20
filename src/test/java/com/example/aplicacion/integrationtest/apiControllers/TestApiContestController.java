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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
		String result;

		result = mockMvc.perform(
			get(badURL).accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isNotFound())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(result, "");

		result = mockMvc.perform(
			get(goodURL).accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();

		assertEquals(convertObjectToJSON(contest.toContestAPI()), result);
	}

	@Test
	public void testAPIAddContest() throws Exception {
		String url = "/API/v1/contest";

		String badContest = "521";
		String goodContest = contest.getNombreContest();
		String badTeam = "521";
		String goodTeam = String.valueOf(owner.getId());
		String description = contest.getDescripcion();

		ContestString cs = new ContestString();
		String result;

		cs.setSalida("contest NAME DUPLICATED");
		when(contestService.creaContest(badContest, badTeam, Optional.of(description))).thenReturn(cs);
		result = mockMvc.perform(
			post(url)
				.characterEncoding("utf8")
				.param("contestName", badContest)
				.param("teamId", badTeam)
				.param("descripcion", description)
		).andExpect(status().isNotFound())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(cs.getSalida(), result);

		when(contestService.creaContest(badContest, goodTeam, Optional.of(description))).thenReturn(cs);
		result = mockMvc.perform(
			post(url)
				.characterEncoding("utf8")
				.param("contestName", badContest)
				.param("teamId", goodTeam)
				.param("descripcion", description)
		).andExpect(status().isNotFound())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(cs.getSalida(), result);

		cs.setSalida("TEAM NOT FOUND");
		when(contestService.creaContest(goodContest, badTeam, Optional.of(description))).thenReturn(cs);
		result = mockMvc.perform(
			post(url)
				.characterEncoding("utf8")
				.param("contestName", goodContest)
				.param("teamId", badTeam)
				.param("descripcion", description)
		).andExpect(status().isNotFound())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(cs.getSalida(), result);

		cs.setSalida("OK");
		cs.setContest(contest);
		when(contestService.creaContest(goodContest, goodTeam, Optional.of(description))).thenReturn(cs);
		result = mockMvc.perform(
			post(url)
				.characterEncoding("utf8")
				.param("contestName", goodContest)
				.param("teamId", goodTeam)
				.param("descripcion", description)
		).andExpect(status().isCreated())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(convertObjectToJSON(contest.toContestAPI()), result);
	}

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
