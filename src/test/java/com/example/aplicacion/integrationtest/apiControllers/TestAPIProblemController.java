package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APIProblemController;
import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.services.ProblemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.DeclareError;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APIProblemController.class)
public class TestAPIProblemController {
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private MockMvc mockMvc;
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

		problem = new Problem();
		problem.setId(244);
		problem.setNombreEjercicio("Ejercicio de prueba");
		problem.setEquipoPropietario(owner);

		when(problemService.getAllProblemas()).thenReturn(List.of(problem));
		when(problemService.getProblem(String.valueOf(problem.getId()))).thenReturn(Optional.of(problem));
	}

	@Test
	public void testAPIGetProblems() throws Exception {
		String url = "/API/v1/problem";
		String result = mockMvc.perform(
			get(url)
		).andExpect(status().isOk())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();

		ProblemAPI problemAPI = problem.toProblemAPI();
		List<ProblemAPI> problems = List.of(problemAPI);
		assertEquals(convertObjectToJSON(problems), result);
	}

	@Test
	//TODO: averiguar como probar la paginación
	public void testAPIGetProblemsWithPagination() {
		fail("Not implemented yet!");
	}

	@Test
	public void testAPIGetProblem() throws Exception {
		String goodProblem = String.valueOf(problem.getId());
		String badProblem = "745";

		String goodURL = "/API/v1/problem/" + goodProblem;
		String badURL = "/API/v1/problem/" + badProblem;

		HttpStatus status = HttpStatus.NOT_FOUND;
		String salida = "ERROR PROBLEM NOT FOUND";
		testGetProblem(badURL, status, salida);

		status = HttpStatus.OK;
		salida = convertObjectToJSON(problem.toProblemAPI());
		testGetProblem(goodURL, status, salida);
	}

	private void testGetProblem(String url, HttpStatus status, String salida) throws Exception {
		String result = mockMvc.perform(
			get(url)
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}

	@Test
	//TODO: averiguar como probar esta parte
	/*
	  WARNING:
		no hay ningun metodo para pasar un objeto como parametro en MockMvc
		y el controlador no acepta JSON
	*/
	public void testAPICreateProblem() throws Exception {
		String url = "/API/v1/problem";
		fail("Averiguar como realizar esta prueba");
		ProblemString ps = new ProblemString();
		ps.setProblem(problem);
		// new problem with same values as the other one but different id
		/*
		Problem newProblem = new Problem();
		newProblem.setId(201);
		newProblem.setNombreEjercicio(problem.getNombreEjercicio());
		newProblem.setEquipoPropietario(problem.getEquipoPropietario());
		ps.setProblem(newProblem);
		*/
		String salida = "OK";
		ps.setSalida(salida);
		HttpStatus status = HttpStatus.OK;

		when(problemService.addProblem(problem)).thenReturn(ps);
		String result = mockMvc.perform(
			post(url).characterEncoding("utf8")
				.contentType(MediaType.APPLICATION_JSON)
				.content(convertObjectToJSON(problem))
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(convertObjectToJSON(problem.toProblemAPI()), result);
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
