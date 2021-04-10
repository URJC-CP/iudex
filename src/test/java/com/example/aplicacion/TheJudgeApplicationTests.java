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
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class TheJudgeApplicationTests {
	private final String baseURL = "http://localhost:8080/API/v1";
	private final String basePathTestFiles = "/src/main/resources/testfiles";
	private RestTemplate restTemplate;
	private ObjectMapper mapper;

	@BeforeEach
	public void init() {
		restTemplate = new RestTemplate();
		mapper = new ObjectMapper();
	}

	@Test
	@DisplayName("Verificar estado inicial de la aplicación")
	public void test1() throws IOException, JSONException, ParseException {
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
	@DisplayName("Crear concurso con el usuario por defecto")
	public void test2() throws IOException {
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
	public void test3() throws IOException {
		String teamId = "6";
		String contesId = "7";
		String badTeamId = "897";
		String badContestId = "576";
		String salida = "";

		// valid file without problem name
		String filename = "primavera.zip";
		String problemName = "";
		salida = "TEAM NOT FOUND";
		testCreateProblemFromZipWithException(filename, problemName, badTeamId, badContestId, salida);

		salida = "CONCURSO NOT FOUND";
		testCreateProblemFromZipWithException(filename, problemName, teamId, badContestId, salida);

		testCreateProblemFromZip(filename, problemName, teamId, contesId);

		// unnamed empty file without problem name
		filename = ".zip.zip";
		salida = "Nombre del problema vacio";
		testCreateProblemFromZipWithException(filename, problemName, teamId, contesId, salida);

		// unnamed empty file with problem name
		filename = ".zip.zip";
		problemName = "pruba vacio";
		salida = "No hay casos de prueba";
		testCreateProblemFromZipWithException(filename, problemName, teamId, contesId, salida);

		// valid file with problem name
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
			restTemplate.postForEntity(createProblemURL, requestEntity, String.class);
		});
		assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
	}

	//realizar entregas
	@Test
	@DisplayName("Realizar entrega")
	public void test4() throws IOException {
		//fail("Not implemented yet!");
		String contestId = "7";
		String anotherContestId = "8";
		String badContestId = "768";
		String problemPrimavera = "1";
		String problemMySQL = "2";
		String badProblemId = "874";
		String teamId = "6";
		String badTeamId = "987";

		String language = "";
		String codeFile = "vacio.java";
		String salida = "CONTEST NOT FOUND";
		testAddSubmissionWithException(badContestId, badProblemId, badTeamId, language, codeFile, salida);

		salida = "PROBLEM NOT FOUND";
		testAddSubmissionWithException(contestId, badProblemId, badTeamId, language, codeFile, salida);

		salida = "TEAM NOT FOUND";
		testAddSubmissionWithException(contestId, problemPrimavera, badTeamId, language, codeFile, salida);

		//salida = "LANGUAGE NOT FOUND"; --> cannot be reached --> 500 : [ERROR GENERAL DEL SISTEMA]
		//language = getLanguage("php");
		//testAddSubmissionWithException(contestId, problemPrimavera, teamId, language, codeFile, salida);

		language = getLanguage("java");
		salida = "PROBLEM NOT IN CONCURSO";
		testAddSubmissionWithException(anotherContestId, problemPrimavera, teamId, language, codeFile, salida);

		// incorrect language selected
		codeFile = "primavera/submissions/accepted/main.java";
		language = getLanguage("python");
		testAddSubmission(contestId, problemPrimavera, teamId, language, codeFile);

		// all okay
		codeFile = "primavera/submissions/accepted/main.java";
		language = getLanguage("java");
		testAddSubmission(contestId, problemPrimavera, teamId, language, codeFile);

		// incorrect file type
		codeFile = ".zip.zip";
		language = getLanguage("java");
		testAddSubmission(contestId, problemPrimavera, teamId, language, codeFile);

		fail("I created a python submission with a java file" +
			"\nI created a submission with an empty zip file");
	}

	private String getLanguage(String language) {
		language = language.toLowerCase().trim();
		switch (language) {
			case "java":
				return "1";
			case "python":
				return "2";
			case "c":
				return "2";
			case "cpp":
			case "c++":
				return "3";
			case "sql":
				return "4";
			default:
				return "1023";
		}
	}

	private void testAddSubmission(String contestId, String problemId, String teamId, String language, String codeFileName) throws IOException {
		String addSubmissionURL = baseURL + "/submission";
		File file = new File("." + basePathTestFiles + "/" + codeFileName);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("problemId", problemId);
		params.add("contestId", contestId);
		params.add("codigo", new FileSystemResource(file));
		params.add("lenguaje", language);
		params.add("teamId", teamId);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(addSubmissionURL, requestEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		System.out.println(response.getBody());

		JsonNode submission = mapper.readTree(response.getBody());
		String submissionId = submission.get("id").asText();
		testGetSubmission(submissionId);
	}

	private void testAddSubmissionWithException(String contestId, String problemId, String teamId, String language, String codeFileName, String salida) {
		String addSubmissionURL = baseURL + "/submission";
		File file = new File("." + basePathTestFiles + "/" + codeFileName);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("problemId", problemId);
		params.add("contestId", contestId);
		params.add("codigo", new FileSystemResource(file));
		params.add("lenguaje", language);
		params.add("teamId", teamId);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
		Exception exception = assertThrows(Exception.class, () -> {
			restTemplate.postForEntity(addSubmissionURL, requestEntity, String.class);
		});
		assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
	}

	@Test
	@DisplayName("Get a submission")
	public void test5() throws IOException {
		String badSubId = "756";
		String subId = "6";

		String salida = "SUBMISSION NOT FOUND";
		testGetSubmissionWithException(badSubId, salida);
		testGetSubmission(subId);
	}

	private void testGetSubmission(String subId) throws IOException {
		String getSubmissionURL = baseURL + "/submission/" + subId;
		ResponseEntity<String> response = restTemplate.getForEntity(getSubmissionURL, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		System.out.println(response.getBody());

		JsonNode submission = mapper.readTree(response.getBody());
		assertThat(submission.get("id").asText(), equalTo(subId));
	}

	private void testGetSubmissionWithException(String subId, String salida) {
		String getSubmissionURL = baseURL + "/submission/" + subId;
		Exception exception = assertThrows(Exception.class, () -> {
			restTemplate.getForEntity(getSubmissionURL, String.class);
		});
		assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
	}
}
