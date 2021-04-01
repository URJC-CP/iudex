package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APITeamController;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import com.example.aplicacion.Pojos.TeamString;
import com.example.aplicacion.services.TeamService;
import com.example.aplicacion.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APITeamController.class)
public class TestAPITeamController {
	private final JSONConverter jsonConverter = new JSONConverter();
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private TeamService teamService;
	private Team team;
	private User user;

	@BeforeEach
	public void init() {
		team = new Team();
		team.setId(305);
		team.setEsUser(false);
		team.setNombreEquipo("Equipo de prueba");

		when(teamService.getTeamFromId(String.valueOf(team.getId()))).thenReturn(Optional.of(team));
		when(teamService.getTeamByNick(team.getNombreEquipo())).thenReturn(Optional.of(team));
		when(teamService.getAllTeams()).thenReturn(List.of(team));
	}

	@Test
	@DisplayName("Get all teams")
	public void testAPIGetTeams() throws Exception {
		String url = "/API/v1/team";
		String salida = jsonConverter.convertObjectToJSON(List.of(team.toTeamAPISimple()));
		String result = mockMvc.perform(
			get(url).characterEncoding("utf8"))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}

	@Test
	@DisplayName("Get Team")
	public void testAPIGetTeam() throws Exception {
		String badTeam = "843";
		String goodTeam = String.valueOf(team.getId());
		String badURL = "/API/v1/team/" + badTeam;
		String goodURL = "/API/v1/team/" + goodTeam;

		String salida = "ERROR, TEAM NOT FOUND";
		HttpStatus status = HttpStatus.NOT_FOUND;
		testGetTeam(badURL, status, salida);

		status = HttpStatus.OK;
		salida = jsonConverter.convertObjectToJSON(team.toTeamAPI());
		testGetTeam(goodURL, status, salida);
	}

	private void testGetTeam(String url, HttpStatus status, String salida) throws Exception {
		String result = mockMvc.perform(
			get(url).characterEncoding("utf8"))
			.andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}

	@Test
	@DisplayName("Create Team")
	public void testAPICreateTeam() throws Exception {
		String badTeam = "";
		String goodTeam = team.getNombreEquipo();
		String url = "/API/v1/team/";

		TeamString ts = new TeamString();
		String salida = "TEAM NAME DUPLICATED";
		ts.setSalida(salida);
		HttpStatus status = HttpStatus.NOT_FOUND;
		when(teamService.crearTeam(badTeam, false)).thenReturn(ts);
		testCreateTeam(url, badTeam, status, salida);

		salida = "OK";
		ts.setSalida(salida);
		ts.setTeam(team);
		status = HttpStatus.OK;
		when(teamService.crearTeam(goodTeam, false)).thenReturn(ts);
		salida = jsonConverter.convertObjectToJSON(team.toTeamAPI());
		testCreateTeam(url, goodTeam, status, salida);
	}

	private void testCreateTeam(String url, String team, HttpStatus status, String salida) throws Exception {
		String result = mockMvc.perform(
			post(url).characterEncoding("utf8")
				.param("nombreEquipo", team))
			.andExpect(status().is(status.value()))
			.andDo(print())
			.andReturn().getResponse()
			.getContentAsString();
		assertEquals(salida, result);
	}

	public void testAPIDeleteTeam(){

	}

	//updateTeam
	//addUserToTeam
	//deleteUserFromTeam
}
