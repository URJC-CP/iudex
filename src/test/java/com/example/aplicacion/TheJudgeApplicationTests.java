package com.example.aplicacion;

import com.example.aplicacion.utils.JSONConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.parser.ParseException;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SpringBootTest
public class TheJudgeApplicationTests {
	private final String baseURL = "http://localhost:8080/API/v1";
	private JSONConverter jsonConverter;
	private RestTemplate restTemplate;
	private ObjectMapper mapper;

	@BeforeEach
	public void init() {
		jsonConverter = new JSONConverter();
		restTemplate = new RestTemplate();
		mapper = new ObjectMapper();
	}

	// verificar estado inicial
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

		JsonNode team = contest.get("teamPropietario");
		System.out.println(team);
		assertThat(team.get("id").asLong(), equalTo(6L));
		assertThat(team.get("nombreEquipo").asText(), equalTo("pavloXd"));
	}

	//crear concurso
	@Test
	@DisplayName("Crear Concurso desde Cero")
	public void testCreateContestFromZero() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// crear usuario y equipo
		String createUserURL = baseURL + "/user";
		String nickname = "pruebas001";
		String email = "prueba@pruebas001.com";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("nickname", nickname);
		params.add("email", email);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(createUserURL, request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		System.out.println(response.getBody());

		JsonNode user = mapper.readTree(response.getBody());
		Long userId = user.get("id").asLong();
		assertThat(user.get("nickname").asText(), equalTo(nickname));
		assertThat(user.get("email").asText(), equalTo(email));

		// verificar que se ha creado el usuario y el equipo
		String getAllTeamsUrl = baseURL + "/team";
		response = restTemplate.getForEntity(getAllTeamsUrl, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));

		JsonNode mainPage = mapper.readTree(response.getBody());
		long teamId = -1;
		JsonNode team = null;

		//buscar equipo
		boolean foundTeam = false;
		for (int i = 0; i < mainPage.size() && !foundTeam; i++) {
			String teamName = mainPage.get(i).get("nombreEquipo").asText();
			if (teamName.equals(nickname)) {
				foundTeam = true;
				team = mainPage.get(i);
			}
		}
		//obtener id del equipo
		//assertThat(team.get("nombreEquipo"), equalTo(nickname));
		teamId = team.get("id").asLong();
		System.out.println(team);

		String contestName = "concurso prueba 001";
		String contestDescription = "concurso de prueba creado por el usuario prueba001";

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
	//crear usuarios
	//crear equipos
	//realizar entregas

	//actualizar datos
	//eliminar datos

	//actualizar problemas
	//eliminar problemas

	//actualizar concurso
	//eliminar concurso

}
