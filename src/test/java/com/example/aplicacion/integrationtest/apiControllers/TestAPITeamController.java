package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APITeamController;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.services.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(APITeamController.class)
public class TestAPITeamController {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private TeamService teamService;

	private Team team;

	@BeforeEach
	public void init() {
		team = new Team();
		team.setId(305);
		team.setNombreEquipo("Equipo de prueba");
	}
}
