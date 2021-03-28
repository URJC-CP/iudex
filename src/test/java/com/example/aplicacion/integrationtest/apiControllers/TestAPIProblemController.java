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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
	@DisplayName("Get All Problems")
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
	@DisplayName("Get All Problems With Pagination")
	@Disabled("Hay que averiguar como probar la paginación")
	public void testAPIGetProblemsWithPagination() {
		//"Not implemented yet!
	}

	@Test
	@DisplayName("Get Selected Problem")
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

	@Test
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
	/*
	  WARNING:
		no hay ningun metodo para pasar un objeto como parametro en MockMvc
		y el controlador no acepta JSON
	*/
	@DisplayName("Create problem Using a Problem Object")
	@Disabled("Hay que averiguar como realizar esta prueba")
	public void testAPICreateProblem() throws Exception {
		String url = "/API/v1/problem";
		ProblemString ps = new ProblemString();
		// new problem with same values as the other one but different id
		/*
		Problem newProblem = new Problem();
		newProblem.setId(201);
		newProblem.setNombreEjercicio(problem.getNombreEjercicio());
		newProblem.setEquipoPropietario(problem.getEquipoPropietario());
		ps.setProblem(newProblem);
		*/
		String salida = "OK";
		HttpStatus status = HttpStatus.OK;
		ps.setSalida(salida);
		ps.setProblem(problem);

		when(problemService.addProblem(problem)).thenReturn(ps);
		String result = mockMvc.perform(
			post(url).characterEncoding("utf8")
				.contentType(MediaType.APPLICATION_JSON)
				.content(convertObjectToJSON(problem))
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(convertObjectToJSON(problem.toProblemAPI()), result);
	}

	@Test
	@DisplayName("Create Problem From Zip")
	@Disabled("Investigar como pasar multipartfile y comprobar que se actualiza el problema la segunda vez")
	public void testAPICreateProblemFromZip() throws Exception {
		File goodFile = new File("prueba2.zip");
		File badFile = new File("vacio.zip");
		goodFile.createNewFile();
		badFile.createNewFile();
		InputStream goodInputStream = new FileInputStream(goodFile);
		InputStream badInputStream = new FileInputStream(badFile);

		String url = "/API/v1/problem/fromZip";
		String badTeam = "756";
		String goodTeam = String.valueOf(owner.getId());
		String badContest = "532";
		String goodContest = String.valueOf(contest.getId());
		String badProblem = "";
		String goodProblem = problem.getNombreEjercicio();
		String badFilename = badFile.getName();
		String goodFilename = goodFile.getName();

		ProblemString ps = new ProblemString();
		String salida = "TEAM NOT FOUND";
		HttpStatus status = HttpStatus.NOT_FOUND;

		when(problemService.addProblemFromZip(badFilename, badInputStream, badTeam, badProblem, badContest)).thenReturn(ps);
		testAddProblemFromZip(url, badFilename, badInputStream, badTeam, badProblem, badContest, status, salida);

		when(problemService.addProblemFromZip(badFilename, badInputStream, badTeam, badProblem, goodContest)).thenReturn(ps);
		testAddProblemFromZip(url, badFilename, badInputStream, badTeam, badProblem, goodContest, status, salida);

		when(problemService.addProblemFromZip(badFilename, badInputStream, badTeam, goodProblem, badContest)).thenReturn(ps);
		testAddProblemFromZip(url, badFilename, badInputStream, badTeam, goodProblem, badContest, status, salida);

		when(problemService.addProblemFromZip(badFilename, badInputStream, badTeam, goodProblem, goodContest)).thenReturn(ps);
		testAddProblemFromZip(url, badFilename, badInputStream, badTeam, goodProblem, goodContest, status, salida);

		when(problemService.addProblemFromZip(goodFilename, goodInputStream, badTeam, badProblem, badContest)).thenReturn(ps);
		testAddProblemFromZip(url, goodFilename, goodInputStream, badTeam, badProblem, badContest, status, salida);

		when(problemService.addProblemFromZip(goodFilename, goodInputStream, badTeam, badProblem, goodContest)).thenReturn(ps);
		testAddProblemFromZip(url, goodFilename, goodInputStream, badTeam, badProblem, goodContest, status, salida);

		when(problemService.addProblemFromZip(goodFilename, goodInputStream, badTeam, goodProblem, badContest)).thenReturn(ps);
		testAddProblemFromZip(url, goodFilename, goodInputStream, badTeam, goodProblem, badContest, status, salida);

		when(problemService.addProblemFromZip(goodFilename, goodInputStream, badTeam, goodProblem, goodContest)).thenReturn(ps);
		testAddProblemFromZip(url, goodFilename, goodInputStream, badTeam, badProblem, goodContest, status, salida);

		salida = "CONCURSO NOT FOUND";
		ps.setSalida(salida);

		when(problemService.addProblemFromZip(badFilename, badInputStream, goodTeam, badProblem, badContest)).thenReturn(ps);
		testAddProblemFromZip(url, badFilename, badInputStream, goodTeam, badProblem, badContest, status, salida);

		when(problemService.addProblemFromZip(badFilename, badInputStream, goodTeam, goodProblem, badContest)).thenReturn(ps);
		testAddProblemFromZip(url, badFilename, badInputStream, goodTeam, goodProblem, badContest, status, salida);

		when(problemService.addProblemFromZip(goodFilename, goodInputStream, goodTeam, badProblem, badContest)).thenReturn(ps);
		testAddProblemFromZip(url, goodFilename, goodInputStream, goodTeam, goodProblem, badContest, status, salida);

		salida = "Nombre del problema vacio";
		ps.setSalida(salida);

		when(problemService.addProblemFromZip(badFilename, badInputStream, goodTeam, badProblem, goodContest)).thenReturn(ps);
		testAddProblemFromZip(url, badFilename, badInputStream, goodTeam, badProblem, goodContest, status, salida);

		when(problemService.addProblemFromZip(goodFilename, goodInputStream, goodTeam, badProblem, goodContest)).thenReturn(ps);
		testAddProblemFromZip(url, goodFilename, goodInputStream, goodTeam, badProblem, goodContest, status, salida);

		//TODO:investigar que sucede en este caso
		when(problemService.addProblemFromZip(badFilename, badInputStream, goodTeam, goodProblem, goodContest)).thenReturn(ps);

		salida = "OK";
		status = HttpStatus.OK;
		ps.setProblem(problem);

		when(problemService.addProblemFromZip(goodFilename, goodInputStream, goodTeam, goodProblem, goodContest)).thenReturn(ps);
		salida = convertObjectToJSON(problem.toProblemAPI());
		testAddProblemFromZip(url, goodFilename, goodInputStream, goodTeam, goodProblem, goodContest, status, salida);

		//verificar que se actualiza la segunda vez --> que llama a update


		//eliminar ficheros creados
		badFile.deleteOnExit();
		goodFile.deleteOnExit();
		badInputStream.close();
		goodInputStream.close();
	}

	private void testAddProblemFromZip(String url, String filename, InputStream is, String team, String problem, String contest, HttpStatus status, String salida) throws Exception {
		String result = mockMvc.perform(
			post(url).characterEncoding("utf8")
				.accept(MediaType.MULTIPART_FORM_DATA_VALUE)
				.param("problemName", problem)
				.param("teamId", team)
				.param("contestId", contest)
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}

	@Test
	@DisplayName("Update Problem")
	@Disabled("Update Problem Not implemented yet!")
	public void testAPIUpdateProblem() {

	}

	@Test
	@DisplayName("Update Problem From Zip")
	@Disabled("Update Problem From Zip Not implemented yet!")
	public void testAPIUpdateProblemFromZip() {

	}

	@Test
	@DisplayName("Get PDF from Problem")
	@Disabled("Falta realizar prueba con un pdf")
	public void testAPIGetPdfFromProblem() throws Exception {
		String goodProblem = String.valueOf(problem.getId());
		String badProblem = "543";
		String goodURL = "/API/v1//problem/" + goodProblem + "/getPDF";
		String badURL = "/API/v1//problem/" + badProblem + "/getPDF";

		String salida = "ERROR PROBLEMA NO ECONTRADO";
		HttpStatus status = HttpStatus.NOT_FOUND;
		testGoToProblem(badURL, status, salida);

		salida = "";
		testGoToProblem(goodURL, status, salida);

		//TODO:probar con un pdf
		File pdf = new File("");
	}

	private void testGoToProblem(String url, HttpStatus status, String salida) throws Exception {
		String result = mockMvc.perform(
			get(url).characterEncoding("utf8")
		).andExpect(status().isNotFound())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}

	@Test
	@DisplayName("Delete problem from all contests")
	public void testAPIDeleteProblemFromALLContests() throws Exception {
		String badProblem = "546";
		String goodProblem = String.valueOf(problem.getId());

		String badURL = "/API/v1/problem/" + badProblem;
		String goodURL = "/API/v1/problem/" + goodProblem;

		String salida = "PROBLEM NOT FOUND";
		HttpStatus status = HttpStatus.NOT_FOUND;
		when(problemService.deleteProblem(badProblem)).thenReturn(salida);
		testDeleteProblem(badURL, status, salida);

		salida = "OK";
		status = HttpStatus.OK;
		when(problemService.deleteProblem(goodProblem)).thenReturn(salida);
		salida = "";
		testDeleteProblem(goodURL, status, salida);
	}

	private void testDeleteProblem(String url, HttpStatus status, String salida) throws Exception {
		String result = mockMvc.perform(
			delete(url)
		).andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
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
