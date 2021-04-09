package com.example.aplicacion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.parser.ParseException;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class TheJudgeApplicationTests {
	private final String baseURL = "http://localhost:8080/API/v1";
	private final String basePathTestFiles = "/src/main/resources/testfiles";
	private final String teamId = "6";
	private RestTemplate restTemplate;
	private ObjectMapper mapper;

	@BeforeEach
	public void init() {
		restTemplate = new RestTemplate();
		mapper = new ObjectMapper();
	}

	@Test
	@DisplayName("Verificar estado inicial de la aplicación")
	public void testInitialState() throws IOException, JSONException, ParseException {
		String url = baseURL + "/contest/?contestId=7";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

		JsonNode lista = mapper.readTree(response.getBody());
		JsonNode contest = lista.get(0);
		System.out.println(contest);
		assertThat(contest.get("id").asLong(), equalTo(7L));
		assertThat(contest.get("nombreContest").asText(), equalTo("contestPrueba"));

		JsonNode teamNode = contest.get("teamPropietario");
		System.out.println(teamNode);
		assertThat(teamNode.get("id").asLong(), equalTo(6L));
		assertThat(teamNode.get("nombreEquipo").asText(), equalTo("pavloXd"));
	}

	@Test
	@DisplayName("Crear concurso desde cero")
	public void testCreateContestFromZero() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = null;
		HttpEntity<MultiValueMap<String, String>> request = null;
		ResponseEntity<String> response = null;

		//buscar equipo
		String getAllTeamsUrl = baseURL + "/team";
		response = restTemplate.getForEntity(getAllTeamsUrl, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));

		JsonNode teams = mapper.readTree(response.getBody());
		JsonNode team = teams.get(0);
		System.out.println(team);

		//obtener id del equipo
		long teamId = team.get("id").asLong();

		String contestName = "concurso prueba 001";
		String contestDescription = "concurso de prueba";

		//crear concurso
		String contestURL = baseURL + "/contest";
		params = new LinkedMultiValueMap<>();
		params.add("contestName", contestName);
		params.add("teamId", String.valueOf(teamId));
		params.add("descripcion", contestDescription);

		request = new HttpEntity<>(params, headers);
		response = restTemplate.postForEntity(contestURL, request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

		JsonNode contest = mapper.readTree(response.getBody());
		assertThat(contest.get("nombreContest").asText(), equalTo(contestName));
		assertThat(contest.get("descripcion").asText(), equalTo(contestDescription));
		assertThat(contest.get("teamPropietario").asText(), equalTo(team.asText()));
		System.out.println(contest);
	}

	//crear problemas
	@Test
	@DisplayName("Crear problema desde un archivo zip")
	public void testCreateProblemFromZip() throws IOException {
		String filename = "primavera.zip";
		String problemName = "";
		String contesId = "7";

		String badTeamId = "897";
		String badContestId = "576";
		String salida = "";

		salida = "TEAM NOT FOUND";
		testCreateProblemFromZipWithException(filename, problemName, badTeamId, badContestId, salida);

		salida = "CONCURSO NOT FOUND";
		testCreateProblemFromZipWithException(filename, problemName, teamId, badContestId, salida);

		// empty problem name
		testCreateProblemFromZip(filename, problemName, teamId, contesId);

		// empty file
		filename = ".zip.zip";
		salida = "Nombre del problema vacio";
		testCreateProblemFromZipWithException(filename, problemName, teamId, contesId, salida);

		filename = ".zip.zip";
		problemName = "pruba vacio";
		salida = "No hay casos de prueba";
		testCreateProblemFromZipWithException(filename, problemName, teamId, contesId, salida);

		// file with problem name
		filename = "pruebaMYSQL.zip";
		problemName = "problema de mysql";
		testCreateProblemFromZip(filename, problemName, teamId, contesId);
	}

	private void testCreateProblemFromZip(String filename, String problemName, String teamId, String contestId) throws IOException {
		String createProblemURL = baseURL + "/problem/fromZip";
		File file = new File("." + basePathTestFiles + "/" + filename);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("file", new FileSystemResource(file));
		params.add("problemName", problemName);
		params.add("teamId", teamId);
		params.add("contestId", contestId);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(createProblemURL, requestEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		System.out.println(response.getBody());

		JsonNode problem = mapper.readTree(response.getBody());
		JsonNode team = problem.get("equipoPropietario");

		assertThat(team.get("id").asText(), equalTo(teamId));
		assertThat(team.get("nombreEquipo").asText(), equalTo("pavloXd"));

		if (problemName.isEmpty()) {
			assertThat(problem.get("nombreEjercicio").asText() + ".zip", equalTo(filename));
		} else {
			assertThat(problem.get("nombreEjercicio").asText(), equalTo(problemName));
		}
	}

	private void testCreateProblemFromZipWithException(String filename, String problem, String team, String contest, String salida) {
		String createProblemURL = baseURL + "/problem/fromZip";
		File file = new File("." + basePathTestFiles + "/" + filename);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("file", new FileSystemResource(file));
		params.add("problemName", problem);
		params.add("teamId", team);
		params.add("contestId", contest);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
		Exception exception = assertThrows(Exception.class, () -> {
			ResponseEntity<String> response = restTemplate.postForEntity(createProblemURL, requestEntity, String.class);
		});
		assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
	}

	//realizar entregas

}
